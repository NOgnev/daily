// Тип пользователя
export interface User {
  id: string;
  nickname: string;
}

// Типы auth контекста
export interface AuthContextType {
  user: User | null | undefined;
  login: (nickname: string, password: string) => Promise<void>;
  register: (nickname: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

export interface Device {
    id: string;
    expiryDate: string;
}
