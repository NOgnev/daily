import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Словарь с переводами ошибок
const resources = {
  en: {
    translation: {
      'USER_NOT_FOUND': 'User not found',
      'USER_ALREADY_EXISTS': 'User already exists',
      'BAD_REQUEST': 'Bad request',
      'FORBIDDEN': 'Forbidden',
      'NOT_FOUND': 'Resource not found',
      'UNPROCESSABLE_ENTITY': 'Validation error',
      'SERVER_ERROR': 'Server error',
      'UNKNOWN_ERROR': 'Unknown error',
      'UNAUTHORIZED': 'Bad credentials',
      'REFRESH_TOKEN_INVALID': 'Authentication error',
    },
  },
  ru: {
    translation: {
      'USER_NOT_FOUND': 'Пользователь не найден',
      'USER_ALREADY_EXISTS': 'Пользователь уже существует',
      'BAD_REQUEST': 'Неверный запрос',
      'FORBIDDEN': 'Доступ запрещен',
      'NOT_FOUND': 'Ресурс не найден',
      'UNPROCESSABLE_ENTITY': 'Ошибка валидации',
      'SERVER_ERROR': 'Ошибка сервера',
      'UNKNOWN_ERROR': 'Неизвестная ошибка',
      'UNAUTHORIZED': 'Неверные учетные данные пользователя',
      'REFRESH_TOKEN_INVALID': 'Ошибка аутентификации',
    },
  },
};

i18n.use(initReactI18next).init({
  resources,
  lng: 'ru', // Устанавливаем язык по умолчанию
  interpolation: {
    escapeValue: false, // React уже безопасен
  },
}).then(() => {
  console.log("i18next инициализирован успешно!");
}).catch((err) => {
  console.error("Ошибка инициализации i18next:", err);
});

export default i18n;
