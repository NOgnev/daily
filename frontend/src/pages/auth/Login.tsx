import { useState } from 'react';
import { Form, Button, Container, Card, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { handleApiError } from '../../utils/handleApiError';
import { validateForm, loginSchema } from '../../utils/validateForm';

const Login = () => {
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [formErrors, setFormErrors] = useState<{ nickname?: string; password?: string }>({});
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);

  const from = location.state?.from?.pathname || '/profile';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const { isValid, errors } = await validateForm(loginSchema, { nickname, password });
    if (!isValid) {
      setFormErrors(errors);
      setLoading(false);
      return;
    }

    setFormErrors({}); // 🧼 Сброс ошибок после успешной валидации

    try {
      await login(nickname, password);
      navigate(from, { replace: true });
      window.scrollTo(0, 0);
    } catch (err) {
      handleApiError(err, setError);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center fade-in" style={{ minHeight: '60vh' }}>
      <Card className="w-100" style={{ maxWidth: '400px' }}>
        <Card.Body>
          <h2 className="text-center mb-4">Log In</h2>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formNickname">
              <Form.Label>Nickname</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter nickname"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                isInvalid={!!formErrors.nickname}
              />
              <Form.Control.Feedback type="invalid">
                {formErrors.nickname}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                isInvalid={!!formErrors.password}
              />
              <Form.Control.Feedback type="invalid">
                {formErrors.password}
              </Form.Control.Feedback>
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100" disabled={loading}>
              {loading ? <Spinner animation="border" size="sm" /> : 'Log In'}
            </Button>
          </Form>

          <div className="w-100 text-center mt-3">
            Don't have an account? <Link to="/register">Register</Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Login;
