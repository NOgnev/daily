import * as Yup from 'yup';
import { nicknameSchema, passwordSchema, loginPasswordSchema } from '../yupLocale';

export const loginSchema = Yup.object({
  nickname: nicknameSchema,
  password: loginPasswordSchema,
});

export const registerSchema = Yup.object({
  nickname: nicknameSchema,
  password: passwordSchema,
});

export async function validateForm<T extends Record<string, any>>(
  schema: Yup.ObjectSchema<any>,
  values: T
): Promise<{ isValid: boolean; errors: Partial<Record<keyof T, string>> }> {
  try {
    await schema.validate(values, { abortEarly: false });
    return { isValid: true, errors: {} };
  } catch (err: unknown) {
    if (err instanceof Yup.ValidationError) {
      const errors: Partial<Record<keyof T, string>> = {};
      err.inner.forEach((e) => {
        if (e.path) {
          errors[e.path as keyof T] = e.message;
        }
      });
      return { isValid: false, errors };
    }

    return {
      isValid: false,
      errors: { general: 'Unexpected validation error.' } as Partial<Record<keyof T, string>>,
    };
  }
}
