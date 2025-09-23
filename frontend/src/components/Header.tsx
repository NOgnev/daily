import { useState } from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import logo from '../logo.svg';

const Header = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [expanded, setExpanded] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setExpanded(false);
  };

  const isActive = (path: string): boolean => location.pathname === path;

  return (
    <Navbar bg="dark" variant="dark" expand="lg" sticky="top" expanded={expanded} onToggle={setExpanded} collapseOnSelect>
      <Container>
        <Navbar.Brand as={Link} to="/">
            <img
                src={logo}
                width="30"
                height="30"
                className="d-inline-block align-top"
                alt="React Bootstrap logo"
            />
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            {user && (
                <>
                    <Nav.Link as={Link} to="/daily" active={isActive('/daily')} eventKey="1">Daily</Nav.Link>
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