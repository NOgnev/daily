import axios, { AxiosError } from 'axios';
import i18n from '../i18n';

interface ErrorResponse {
  error?: string;
  message?: string;
}

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ErrorResponse>(error)) {
    const errorData = error.response?.data;

    if (!error.response) {
      return i18n.t('NETWORK_ERROR');
    }

    if (errorData?.error) {
      const translated = i18n.t(errorData.error);

      if (translated !== errorData.error) {
        return translated;
      }
    }
  }

  return i18n.t('UNKNOWN_ERROR');
}

export function handleApiError(
  error: unknown,
  setError: React.Dispatch<React.SetStateAction<string | null>>
) {
  const message = getErrorMessage(error);
  setError(message);
}
