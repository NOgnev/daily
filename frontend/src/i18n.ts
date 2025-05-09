import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Словарь с переводами ошибок
const resources = {
  en: {
    translation: {
      // API ошибки
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

      // Валидация
      'validation.required': 'This field is required',
      'validation.min': 'Minimum {{min}} characters',
      'validation.max': 'Maximum {{max}} characters',
      'validation.matches.nickname': 'Nickname can contain only letters, digits, underscores, and periods',
      'validation.matches.password': 'Password must include at least one uppercase letter, one number, and one special character (@$!%*?&)',
      'validation.matches.default': 'Invalid format',
    },
  },
  ru: {
    translation: {
      // API ошибки
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

      // Валидация
      'validation.required': 'Это поле обязательно',
      'validation.min': 'Минимум {{min}} символа',
      'validation.max': 'Максимум {{max}} символов',

      // Разные сообщения для matches
      'validation.matches.nickname': 'Никнейм может содержать только буквы латинского алфавита, цифры, точку и символ подчёркивания',
      'validation.matches.password': 'Пароль должен содержать хотя бы одну заглавную букву, одну цифру и один специальный символ (@$!%*?&)',
      'validation.matches.default': 'Неверный формат',
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
