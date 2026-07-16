import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<200'],
  },
};

export default function () {
  const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

  const menusRes = http.get(`${BASE_URL}/menus`);
  check(menusRes, {
    'GET /menus status is 200': (r) => r.status === 200,
  });

  sleep(1);

  const popularRes = http.get(`${BASE_URL}/menus/popular?days=7`);
  check(popularRes, {
    'GET /menus/popular status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
