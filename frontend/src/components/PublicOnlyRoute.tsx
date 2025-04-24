import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Spinner } from 'react-bootstrap';

const PublicOnlyRoute = () => {
  const { isAuthenticated, checkAuth } = useAuth();

  if (isAuthenticated === null) {
    checkAuth();
    return <Spinner animation="border" />;
  }

  return !isAuthenticated ? <Outlet /> : <Navigate to="/profile" replace />;
};
export default PublicOnlyRoute;