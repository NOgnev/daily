import { useEffect, useCallback } from 'react';
import { Card, Container, Button, Spinner, Alert, ListGroup } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
import { useState } from 'react';
import { deviceApi } from '../api/deviceApi';
import { Device } from '../types/authTypes';
import useStorageListener from '../hooks/useStorageListener';

const Profile = () => {
  const { user, logout } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [devices, setDevices] = useState<Device[] | null>(null);

  const fetchDevices = useCallback(async () => {
      setLoading(true);
      try {
          const devices = await deviceApi.getDevices();
          setDevices(devices);
      } catch (err) {
          setError(err instanceof Error? err.message : 'Something went wrong');
      } finally {
          setLoading(false);
      }
  }, []);

  useEffect(() => {
      if (!user) return;
      fetchDevices();
  }, [user, fetchDevices]);

  useStorageListener('user', fetchDevices);

  if (!user) {
    return (
      <Container className="mt-5">
        <Alert variant="warning">User not authenticated</Alert>
      </Container>
    );
  }

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
    <Container className="mt-5 fade-in">
      <Card className="shadow-sm">
        <Card.Body>
          <Card.Title className="mb-4">User Profile</Card.Title>

          <div className="mb-4">
            <h6>Personal Information</h6>
            <hr />
            <p><strong>Id:</strong> {user?.id}</p>
            <p><strong>Nickname:</strong> {user?.nickname}</p>
            {/*<p><strong>Email:</strong> {user?.email || 'Not set'}</p>*/}
          </div>

          <div className="mb-4">
            <h6>Devices</h6>
            <hr />
            {devices ? (
              <ListGroup>
                {devices.map(device => (
                  <ListGroup.Item key={device.id}>
                    <strong>ID:</strong> {device.id} <br />
                    <strong>Expiry Date:</strong> {new Date(device.expiryDate).toLocaleDateString()}
                  </ListGroup.Item>
                ))}
              </ListGroup>
            ) : (
              <p>No devices found.</p>
            )}
          </div>

          <div className="mb-4">
            <h6>Security</h6>
            <hr />
            <Button variant="outline-danger" onClick={logout}>
              Logout from all devices
            </Button>
          </div>

          <div className="text-muted small mt-4">
            <p>Date: {new Date().toLocaleDateString()}</p>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Profile;
