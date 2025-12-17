import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
    stages: [
        { duration: '1m', target: 50 },
        { duration: '2m', target: 100 },
        { duration: '3m', target: 200 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'],
        http_req_failed: ['rate<0.1'],
        errors: ['rate<0.15'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const payload = JSON.stringify({
        content: `Stress test message ${Date.now()} from VU-${__VU}`,
        sender: `stress-user-${__VU}@example.com`,
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

    const success = check(response, {
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

    errorRate.add(!success);

    sleep(0.5);
}

export function setup() {
    console.log('Starting STRESS test - High load scenario');
    console.log(`Base URL: ${BASE_URL}`);
    console.log('This test will push your system to its limits!');
}

export function teardown(data) {
    console.log('Stress test completed');
}