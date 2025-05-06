import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Spinner, Container } from 'react-bootstrap';
import React from 'react';

const PrivateRoute = () => {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return (
          <Container className="d-flex justify-content-center mt-5">
            <Spinner animation="border" />
          </Container>
    );
  }

  return user ? <Outlet /> : <Navigate to="/login" replace />;
};

export default React.memo(PrivateRoute);