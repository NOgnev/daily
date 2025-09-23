import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { authApi } from '../api/authApi';
import { userApi } from '../api/userApi';
import { User, AuthContextType } from '../types/authTypes';
import useStorageListener from '../hooks/useStorageListener';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [loading, setLoading] = useState(true); // пока непонятно зачем
  const [user, setUser] = useState<User | null | undefined>(undefined);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const userData = await userApi.getCurrentUser();
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
      } catch (error) {
        console.error('Failed to load user:', error);
        setUser(null);
        localStorage.removeItem('user');
      } finally {
        setLoading(false);
      }
    };
    loadUser();
  }, []);

  useEffect(() => {
    const handleForceLogout = () => {
      setUser(null);
      localStorage.removeItem('user');
    };

    window.addEventListener('force-logout', handleForceLogout);
    return () => window.removeEventListener('force-logout', handleForceLogout);
  }, []);

  const syncFromStorage = () => {
    const stored = localStorage.getItem('user');
    setUser(stored ? JSON.parse(stored) : null);
  };

  useStorageListener('user', syncFromStorage);

  const handleRegister = useCallback(async (nickname: string, password: string) => {
    setLoading(true);
    try {
      await authApi.register(nickname, password);
      const loginUser = await authApi.login(nickname, password);
      localStorage.setItem('user', JSON.stringify(loginUser));
      setUser(loginUser);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleLogin = useCallback(async (nickname: string, password: string) => {
    setLoading(true);
    try {
      const loginUser = await authApi.login(nickname, password);
      localStorage.setItem('user', JSON.stringify(loginUser));
      setUser(loginUser);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleLogout = useCallback(async () => {
    setLoading(true);
    try {
      await authApi.logout();
      localStorage.removeItem('user');
      setUser(null);
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  const value = useMemo(() => ({
    user,
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout
  }), [user, handleLogin, handleRegister, handleLogout]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};