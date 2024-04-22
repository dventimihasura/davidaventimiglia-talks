import http from 'k6/http';
import { check } from 'k6';
import { sleep } from 'k6';

export const options = {
    thresholds: {
	http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }],
	http_req_duration: ['p(99)<1000'],
    },
    scenarios: {
	average_load: {
	    executor: 'ramping-vus',
	    stages: [
		{ duration: '300s', target: 1 },
		// { duration: '10s', target: 20 },
		// { duration: '10s', target: 40 },
		// { duration: '10s', target: 80 },
		// { duration: '10s', target: 160 },
		// { duration: '10s', target: 320 },
		// { duration: '10s', target: 640 },
		// { duration: '10s', target: 1024 },
		// { duration: '10s', target: 2048 },
		// { duration: '10s', target: 4096 },
		// { duration: '10s', target: 8192 },
	    ],
	},
    },
};

const query = `
query {
  order(limit: 10) {
    id
    status
    account {
      id
      name 
    }
    order_details {
      units
      product {
        name
        price
      }
    }
  }
}
`;

const headers = {
    'Content-Type': 'application/json',
};

export default function() {
    const res = http.post('http://34.94.25.86/graphql', JSON.stringify({ query: query }), {
	headers: headers,
    })
    check(res, {
	'response code was 200': (res) => res.status == 200,
    });
}
