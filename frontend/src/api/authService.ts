import axios from './apiClient';

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: {
    id: string;
    email: string;
    name: string;
  };
}

export const login = async (data: { email: string; password: string; deviceId: string }) => {
  const response = await axios.post<LoginResponse>('/auth/login', data);
  return response.data;
};

export const register = async (data: {
  email: string;
  password: string;
  name: string;
  deviceId: string
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