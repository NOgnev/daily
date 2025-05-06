import { useState } from 'react';
import { Form, Button, Container, Card, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';
import { Link, useNavigate } from 'react-router-dom';

const Register = () => {
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await register(nickname, password);
      navigate('/profile');

      // Прокрутка страницы наверх после навигации
      window.scrollTo(0, 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center fade-in" style={{ minHeight: '100vh' }}>
      <Card className="w-100" style={{ maxWidth: '400px' }}>
        <Card.Body>
          <h2 className="text-center mb-4">Create Account</h2>
          {error && <Alert variant="danger">{error}</Alert>}
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formNickname">
              <Form.Label>Nickname</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter nickname"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                minLength={6}
                required
              />
            </Form.Group>

            <Button
              variant="primary"
              type="submit"
              className="w-100"
              disabled={loading}
            >
              {loading ? <Spinner animation="border" size="sm" /> : 'Register'}
            </Button>
          </Form>
          <div className="w-100 text-center mt-3">
            Already have an account? <Link to="/login">Log In</Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Register;
