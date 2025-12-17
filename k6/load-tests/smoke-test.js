import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
    stages: [
        { duration: '30s', target: 10 },
        { duration: '1m', target: 50 },
        { duration: '2m', target: 50 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
        errors: ['rate<0.1'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {

    const payload = JSON.stringify({
        content: `Test message ${Date.now()}`,
        sender: `user-${__VU}@example.com`,
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
                return body.correlationId !== undefined && body.correlationId !== null;
            } catch (e) {
                return false;
            }
        },
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    errorRate.add(!success);

    sleep(1);
}

export function setup() {
    console.log('Starting load test for Send Message API');
    console.log(`Base URL: ${BASE_URL}`);
}

export function teardown(data) {
    console.log('Load test completed');
}