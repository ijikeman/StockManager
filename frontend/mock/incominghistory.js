import { MockMethod } from 'vite-plugin-mock';

export default [
  {
    url: '/api/incominghistory',
    method: 'get',
    response: () => {
      console.log('HITTING INCOMING HISTORY MOCK');
      return [];
    },
  },
];