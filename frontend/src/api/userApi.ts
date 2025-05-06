import { fetchWithAuth } from './fetchWithAuth';
import { User } from '../types/authTypes';

export const userApi = {
    getCurrentUser: () =>
        fetchWithAuth<User>({
            url: '/user',
            method: 'GET',
        }),
}