import { Container } from 'react-bootstrap';
import { Outlet } from 'react-router-dom';
import Header from './Header';

function Layout() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <Header />
      <Container className="flex-grow-1 mt-4">
        <Outlet /> {/* Здесь будут отображаться дочерние страницы */}
      </Container>
    </div>
  );
}

export default Layout;

