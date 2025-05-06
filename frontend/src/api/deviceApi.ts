import { fetchWithAuth } from './fetchWithAuth';
import { Device } from '../types/authTypes';

export const deviceApi = {
    getDevices: () =>
        fetchWithAuth<Device[]>({
            url: '/device',
            method: 'GET',
        }),
}