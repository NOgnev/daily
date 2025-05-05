import axios from './apiClient';
import { LoginResponse, Device } from '../types/authTypes';


export const login = async (data: { nickname: string; password: string; deviceId: string }) => {
  const headers = {
    'x-device-id': data.deviceId,
  };
  const response = await axios.post<LoginResponse>('/auth/login', data, { headers });
  return response.data;
};

export const register = async (data: {
  nickname: string;
  password: string;
}) => {
  const response = await axios.post<LoginResponse>('/auth/register', data);
  return response.data;
};

export const logout = async (refreshToken: string, deviceId: string) => {
  await axios.post('/auth/logout', { refreshToken, deviceId });
};

export const refresh = async (refreshToken: string, deviceId: string) => {
  const response = await axios.post<Omit<LoginResponse, 'user'>>('/auth/refresh', {
    refreshToken,
    deviceId
  });
  return response.data;
};

export const getDevices = async () => {
  const response = await axios.get<Array<Device>>('/device');
  return response.data;
};
