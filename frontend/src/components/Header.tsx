import { Navbar, Nav, Button, Container } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Navbar bg="dark" variant="dark" collapseOnSelect expand="lg" >
      <Container>
        <Navbar.Brand as={Link} to="/">My App</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/about" eventKey="i">About</Nav.Link>
            {isAuthenticated && (
              <Nav.Link as={Link} to="/profile" eventKey="i">Profile</Nav.Link>
            )}
          </Nav>
          <Nav>
            {isAuthenticated ? (
              <Button variant="outline-light" onClick={handleLogout}>Logout</Button>
            ) : (
              <>
                <Nav.Link as={Link} to="/login" eventKey="i">Login</Nav.Link>
                <Nav.Link as={Link} to="/register" eventKey="i">Register</Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};
export default Header;