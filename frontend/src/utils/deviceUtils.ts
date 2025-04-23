import { v4 as uuidv4 } from 'uuid';

export const generateDeviceId = (): string => {
  let deviceId = localStorage.getItem('deviceId');

  if (!deviceId) {
    deviceId = uuidv4();
    localStorage.setItem('deviceId', deviceId);
  }

  return deviceId;
};

export const getDeviceId = (): string | null => {
  return localStorage.getItem('deviceId');
};