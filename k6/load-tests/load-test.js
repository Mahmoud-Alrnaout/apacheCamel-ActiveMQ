import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1,
    duration: '1m',
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.01'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const payload = JSON.stringify({
        content: 'Smoke test message',
        sender: 'smoke-test@example.com',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(
        `${BASE_URL}/api/messages/send`,
        payload,
        params
    );

    check(response, {
        'status is 200': (r) => r.status === 200,
        'response has correlationId': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.correlationId !== undefined;
            } catch (e) {
                return false;
            }
        },
    });

    sleep(1);
}

export function setup() {
    console.log('Running smoke test...');
    console.log(`Testing: ${BASE_URL}/api/messages/send`);
}