import { Container } from 'react-bootstrap';
import { Outlet } from 'react-router-dom';
import Header from './Header';

const Layout = () => {
  return (
    <div className="d-flex flex-column min-vh-100 fade-in">
      <Header />
      <Container className="flex-grow-1 mt-4">
        <Outlet /> {/* Здесь будут отображаться дочерние страницы */}
      </Container>
    </div>
  );
};

export default Layout;
