import * as Yup from 'yup';
import i18n from './i18n';

Yup.setLocale({
  mixed: {
    required: () => i18n.t('validation.required'),
  },
  string: {
    min: ({ min }) => i18n.t('validation.min', { min }),
    max: ({ max }) => i18n.t('validation.max', { max }),
    matches: ({ path }) => {
      switch (path) {
        case 'nickname':
          return i18n.t('validation.matches.nickname');
        case 'password':
          return i18n.t('validation.matches.password');
        default:
          return i18n.t('validation.matches.default');
      }
    },
  },
});

export const nicknameSchema = Yup.string()
  .min(3)
  .max(20)
  .matches(/^[a-zA-Z0-9_.]+$/)
  .required();

export const passwordSchema = Yup.string()
  .min(8)
  .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/)
  .required();

export const loginPasswordSchema = Yup.string()
  .required();
