import { useState } from 'react';
import { Navbar, Nav, Button, Container, Dropdown, DropdownButton } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setExpanded(false);
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg" sticky="top" expanded={expanded} onToggle={setExpanded} collapseOnSelect>
      <Container>
        <Navbar.Brand as={Link} to="/">DiaryAi</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            {user && (
                <>
                    <Nav.Link as={Link} to="/profile" eventKey="2">Profile</Nav.Link>
                    <Nav.Link as={Link} to="/diary" eventKey="3">Diary</Nav.Link>
                </>
            )}
            <Nav.Link as={Link} to="/about" eventKey="1">About</Nav.Link>
          </Nav>
          <Nav>
            {user ? (
            <>
              <Navbar.Text className="nav-link">
                Signed in as: {user?.nickname}
              </Navbar.Text>
              <Button variant="outline-light" size="sm" onClick={handleLogout}>Logout</Button>
            </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login" eventKey="4">Login</Nav.Link>
                <Nav.Link as={Link} to="/register" eventKey="5">Register</Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};
export default Header;