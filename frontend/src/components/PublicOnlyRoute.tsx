import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Spinner, Container } from 'react-bootstrap';
import React from 'react';

const PublicOnlyRoute = () => {
  const { user } = useAuth();
  const location = useLocation();

  if (user === undefined) {
    return (
          <Container className="d-flex justify-content-center mt-5">
            <Spinner animation="border" />
          </Container>
    );
  }

  return !user ? <Outlet /> : <Navigate to={location.state?.from?.pathname || '/diary'} replace />;
};
export default PublicOnlyRoute;