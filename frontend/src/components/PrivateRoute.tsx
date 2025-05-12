import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Spinner, Container } from 'react-bootstrap';
import React from 'react';

const PrivateRoute = () => {
  const { user } = useAuth();
  const location = useLocation();

  if (user === undefined) {
    return (
          <Container className="d-flex justify-content-center mt-3">
            <Spinner animation="border" />
          </Container>
    );
  }

  return user ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} />;
};

export default React.memo(PrivateRoute);