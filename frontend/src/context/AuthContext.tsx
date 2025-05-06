import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { authApi } from '../api/authApi';
import { userApi } from '../api/userApi';
import { User, AuthContextType } from '../types/authTypes';
import useStorageListener from '../hooks/useStorageListener';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<User | null>(null);

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
        setIsLoading(false);
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

//   useEffect(() => {
//     const interval = setInterval(async () => {
//       try {
//         const userData = await userApi.getCurrentUser();
//         setUser(userData);
//         localStorage.setItem('user', JSON.stringify(userData));
//       } catch (error) {
//         console.error('Failed to refresh user:', error);
//         setUser(null);
//         localStorage.removeItem('user');
//       }
//     }, 60000); // Check every minute
//
//     return () => clearInterval(interval);
//   }, []);

const handleRegister = useCallback(async (nickname: string, password: string) => {
    setIsLoading(true);
    try {
      await authApi.register(nickname, password); // Register the user
      // Immediately log in the user after successful registration
      const loginUser = await authApi.login(nickname, password);
      localStorage.setItem('user', JSON.stringify(loginUser));
      setUser(loginUser);
    } catch (error) {
      console.error('Registration or login failed:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const handleLogin = useCallback(async (nickname: string, password: string) => {
    setIsLoading(true);
    try {
      const loginUser = await authApi.login(nickname, password);
      localStorage.setItem('user', JSON.stringify(loginUser));
      setUser(loginUser);
    } catch (error) {
      console.error('Login failed:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const handleLogout = useCallback(async () => {
    setIsLoading(true);
    try {
      await authApi.logout();
      localStorage.removeItem('user');
      setUser(null);
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const value = useMemo(() => ({
    user,
    isLoading,
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout
  }), [user, isLoading, handleLogin, handleRegister, handleLogout]);

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