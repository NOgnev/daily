import { useEffect } from 'react';
import { Card, Container, Button, Spinner, Alert } from 'react-bootstrap';
import { useAuth } from '../hooks/useAuth';
import { useState } from 'react';

const Profile = () => {
  const { user, logout, checkAuth } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const verifyAuth = async () => {
      try {
        await checkAuth();
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Session expired');
      } finally {
        setLoading(false);
      }
    };
    verifyAuth();
  }, [checkAuth]);

  if (loading) {
    return (
      <Container className="d-flex justify-content-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-4">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <Card className="shadow-sm">
        <Card.Body>
          <Card.Title className="mb-4">User Profile</Card.Title>

          <div className="mb-4">
            <h6>Personal Information</h6>
            <hr />
            <p><strong>Name:</strong> {user?.name || 'Not specified'}</p>
            <p><strong>Email:</strong> {user?.email}</p>
          </div>

          <div className="mb-4">
            <h6>Security</h6>
            <hr />
            <Button variant="outline-danger" onClick={logout}>
              Logout from all devices
            </Button>
          </div>

          <div className="text-muted small mt-4">
            <p>Account created on: {new Date().toLocaleDateString()}</p>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Profile;
