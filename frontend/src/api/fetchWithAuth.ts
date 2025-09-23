import { AxiosRequestConfig, AxiosResponse } from 'axios';
import { apiClient } from './apiClient';

let isRefreshing = false;

let failedQueue: {
  resolve: (value: any) => void;
  reject: (error: any) => void;
  config: AxiosRequestConfig;
}[] = [];

function processQueue(error: any = null, tokenRefreshed: boolean = false) {
  failedQueue.forEach(({ resolve, reject, config }) => {
    if (tokenRefreshed) {
      resolve(fetchWithAuth(config));
    } else {
      reject(error);
    }
  });
  failedQueue = [];
}

export async function fetchWithAuth<T = any>(
  config: AxiosRequestConfig
): Promise<T> {
  const requestConfig: AxiosRequestConfig = {
    ...config,
    withCredentials: true,
  };

  try {
    const response: AxiosResponse<T> = await apiClient(requestConfig);
    return response.data;
  } catch (error: any) {
    const status = error?.response?.status;

    if (status === 401) {
      if (isRefreshing) {
        return new Promise<T>((resolve, reject) => {
          failedQueue.push({ resolve, reject, config: requestConfig });
        });
      }

      isRefreshing = true;

      try {
        await apiClient.post('/auth/refresh');
        isRefreshing = false;
        processQueue(null, true);

        const retryResponse: AxiosResponse<T> = await apiClient(requestConfig);
        return retryResponse.data;
      } catch (refreshError) {
        isRefreshing = false;
        processQueue(refreshError, false);

        window.dispatchEvent(new Event('force-logout'));
        throw refreshError;
      }
    }

    throw error;
  }
}