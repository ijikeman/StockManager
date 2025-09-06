import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

let owners = Mock.mock({
  'list|3-5': [{
    'id|+1': 1,
    'name': '@name',
  }]
}).list;

export default [
  {
    url: '/api/owners',
    method: 'get',
    response: () => {
      return owners;
    },
  },
  {
    url: '/api/owners/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      owners = owners.filter(owner => owner.id !== id);
      return { success: true };
    },
  },
  {
    url: '/api/owners',
    method: 'post',
    response: ({ body }) => {
        const newOwner = { ...body, id: Mock.Random.integer(100, 1000) };
        owners.push(newOwner);
        return newOwner;
    }
  },
  {
      url: '/api/owners/:id',
      method: 'put',
      response: ({ url, body }) => {
        const id = parseInt(url.split('/').pop());
        const index = owners.findIndex(owner => owner.id === id);
        if (index !== -1) {
          owners[index] = { ...owners[index], ...body };
          return owners[index];
        }
        return null;
      }
  }
];
