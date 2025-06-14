import axios from 'axios';

export const apiClient = axios.create({
  baseURL: '/api',
  withCredentials: true,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
});
