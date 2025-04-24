import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Spinner } from 'react-bootstrap';

const PrivateRoute = () => {
  const { isAuthenticated, checkAuth } = useAuth();

  if (isAuthenticated === null) {
    checkAuth();
    return <Spinner animation="border" />;
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
};
export default PrivateRoute;