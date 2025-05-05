import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Spinner, Container } from 'react-bootstrap';

const PrivateRoute = () => {
  const { isAuthenticated, checkAuth } = useAuth();

  if (isAuthenticated === null) {
    checkAuth();
    return (
          <Container className="d-flex justify-content-center mt-5">
            <Spinner animation="border" />
          </Container>
    );
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
};
export default PrivateRoute;