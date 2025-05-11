import axios, { AxiosError } from 'axios';
import i18n from '../i18n'; // Убедись, что путь правильный

// Интерфейс для ожидаемого формата ошибки с сервера
interface ErrorResponse {
  error?: string;
  message?: string;
}

// Функция получения читаемого (переведённого) текста ошибки
export function getErrorMessage(error: unknown): string {
  // Проверяем, является ли это ошибкой от Axios и содержит ли нужные поля
  if (axios.isAxiosError<ErrorResponse>(error)) {
    const errorData = error.response?.data;

    // Если ошибка связана с сетью (например, сервер недоступен)
    if (!error.response) {
      return i18n.t('NETWORK_ERROR');
    }

    if (errorData?.error) {
      const translated = i18n.t(errorData.error);

      // Возвращаем перевод, если он есть в словаре
      if (translated !== errorData.error) {
        return translated;
      }
    }
  }

  // Обработка обычных ошибок JS
  if (error instanceof Error) {
    return i18n.t('UNKNOWN_ERROR');
  }

  // В случае любых других неожиданных ошибок
  return i18n.t('UNKNOWN_ERROR');
}

// Основной обработчик, вызывающий setError с переведённым сообщением
export function handleApiError(
  error: unknown,
  setError: React.Dispatch<React.SetStateAction<string | null>>
) {
  const message = getErrorMessage(error);
  setError(message);
}
