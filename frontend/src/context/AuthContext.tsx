import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import { login, register, logout, refresh } from '../api/authApi';
import { User, LoginData, AuthContextType } from '../types/authTypes';
import useStorageListener from '../hooks/useStorageListener';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [cookies, setCookie, removeCookie] = useCookies(['accessToken', 'refreshToken']);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<User | null>(() => {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
  });

  const syncFromStorage = () => {
      const stored = localStorage.getItem('user');
      setUser(stored ? JSON.parse(stored) : null);
  }
  useStorageListener('user', syncFromStorage);

  const checkAuth = useCallback(async () => {
    setIsLoading(true);
    try {
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = localStorage.getItem('refreshToken');
        const user = localStorage.getItem('user');
//         if (cookies.accessToken) {
        if (accessToken && !isTokenExpired(accessToken) && user) {
            setUser(JSON.parse(user));
            setIsAuthenticated(true);
//         } else if (cookies.refreshToken) {
        } else if (refreshToken) {
            const deviceId = localStorage.getItem('deviceId');
            if (deviceId) {
//                 const { accessToken, refreshToken } = await refresh(cookies.refreshToken, deviceId);
                const { accessToken : newAccessToken, refreshToken : newRefreshToken } = await refresh(refreshToken, deviceId);
                setAuthTokens(newAccessToken, newRefreshToken);
            }
        } else {
            clearAuth();
        }
      } catch (error) {
          clearAuth();
      } finally {
          setIsLoading(false);
      }
  }, [/*cookies.accessToken, cookies.refreshToken*/ ]);

  useEffect(() => {
    const interval = setInterval(() => {
      const accessToken = localStorage.getItem('accessToken');
      if (accessToken && isTokenExpired(accessToken)) {
        checkAuth();
      }
    }, 60000); // Check every minute
    return () => clearInterval(interval);
  }, [checkAuth]);

  const setAuthTokens = (accessToken: string, refreshToken: string) => {
//     setCookie('accessToken', accessToken, { path: '/', httpOnly: true, secure: true });
//     setCookie('refreshToken', refreshToken, { path: '/', httpOnly: true, secure: true });
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setIsAuthenticated(true);
  };

  const clearAuth = () => {
//     removeCookie('accessToken', { path: '/' });
//     removeCookie('refreshToken', { path: '/' });
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };

  const handleLogin = async (loginData: LoginData) => {
    setIsLoading(true);
    try {
        const deviceId = localStorage.getItem('deviceId');
        if (!deviceId) throw new Error('Device ID not found');

        const { accessToken, refreshToken, user } = await login({ ...loginData, deviceId });
        setAuthTokens(accessToken, refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
        setUser(user);
    } finally {
        setIsLoading(false);
    }
  };

  const handleRegister = async (registerData: LoginData) => {
    setIsLoading(true);
    try {
        const { accessToken, refreshToken, user } = await register(registerData);
        setAuthTokens(accessToken, refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
        setUser(user);
    } finally {
        setIsLoading(false);
    }
  };

  const handleLogout = async () => {
    setIsLoading(true);
    try {
        const deviceId = localStorage.getItem('deviceId');
        if (deviceId && cookies.refreshToken) {
          await logout(cookies.refreshToken, deviceId);
        }
        clearAuth();
    } finally {
        setIsLoading(false);
    }
  };

  const isTokenExpired = (token: string): boolean => {
    const decodedToken = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    return decodedToken.exp < currentTime;
  };

  const value = useMemo(() => ({
      user,
      isAuthenticated,
      isLoading,
      checkAuth,
      login: handleLogin,
      register: handleRegister,
      logout: handleLogout
  }), [user, isAuthenticated, isLoading]);

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