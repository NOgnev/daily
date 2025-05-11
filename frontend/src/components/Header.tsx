import { useState } from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation(); // для отслеживания текущего пути
  const [expanded, setExpanded] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setExpanded(false);
  };

  // Явно указываем тип параметра 'path' как string
  const isActive = (path: string): boolean => location.pathname === path; // Функция для проверки активной ссылки

  return (
    <Navbar bg="dark" variant="dark" expand="lg" sticky="top" expanded={expanded} onToggle={setExpanded} collapseOnSelect>
      <Container>
        <Navbar.Brand as={Link} to="/">dAIly</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            {user && (
                <>
                    <Nav.Link as={Link} to="/diary" active={isActive('/diary')} eventKey="1">Diary</Nav.Link>
                    <Nav.Link as={Link} to="/daily" active={isActive('/daily')} eventKey="6">Daily</Nav.Link>
                </>
            )}
            <Nav.Link as={Link} to="/about" active={isActive('/about')} eventKey="2">About</Nav.Link>
          </Nav>
          <Nav>
            {user ? (
                <Nav.Link as={Link} to="/profile" active={isActive('/profile')} eventKey="3">{user?.nickname}</Nav.Link>
            ) : (
              <>
                <Nav.Link as={Link} to="/login" active={isActive('/login')} eventKey="4">Login</Nav.Link>
                <Nav.Link as={Link} to="/register" active={isActive('/register')} eventKey="5">Register</Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};
export default Header;