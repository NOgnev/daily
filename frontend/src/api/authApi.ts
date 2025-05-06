import { apiClient } from './apiClient';
import { User } from '../types/authTypes';

export const authApi = {
    register: (nickname: string, password: string): Promise<User> =>
        apiClient.post<User>('/auth/register', { nickname, password }).then(res => res.data),
    login: (nickname: string, password: string): Promise<User> =>
        apiClient.post<User>('/auth/login', { nickname, password }).then(res => res.data),
    refresh: (): Promise<void> =>
        apiClient.post('/auth/refresh').then(res => res.data),
    logout: (): Promise<void> =>
        apiClient.post('/auth/logout').then(res => res.data),
}