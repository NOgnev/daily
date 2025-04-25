// Типы для данных аутентификации
export interface LoginData {
  nickname: string;
  password: string;
}

// export interface RegisterData extends LoginData {
//   name: string;
// }

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

// Тип пользователя
export interface User {
  id: string;
  nickname: string;
}

// Типы auth контекста
export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (data: LoginData) => Promise<void>;
  register: (data: LoginData) => Promise<void>;
  logout: () => Promise<void>;
  checkAuth: () => Promise<void>;
}

// Типы для ошибок аутентификации
export type AuthError = {
  message: string;
  code?: string;
  status?: number;
};

// Тип для payload JWT токена
export interface JwtPayload {
  userId: string;
  email: string;
  exp: number;
  iat: number;
  deviceId?: string;
}

// Типы для HTTP-ответов
export interface ApiResponse<T> {
  data?: T;
  error?: AuthError;
  status: number;
}

// Типы для работы с cookies
export interface CookieOptions {
  path?: string;
  expires?: Date;
  maxAge?: number;
  domain?: string;
  secure?: boolean;
  httpOnly?: boolean;
  sameSite?: 'strict' | 'lax' | 'none';
}

export interface Device {
    id: string;
    expiryDate: string;
}
