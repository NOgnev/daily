import { Container, Card, ListGroup } from 'react-bootstrap';

const About = () => {
  return (
    <Container className="mt-5 fade-in">
      <Card className="shadow-sm">
        <Card.Body>
          <Card.Title className="mb-4">About Our Service</Card.Title>

          <Card.Text>
            Welcome to our secure SPA application with JWT authentication.
            This demo showcases best practices for React and Spring Boot integration.
          </Card.Text>

          <h5 className="mt-4 mb-3">Features</h5>
          <ListGroup variant="flush">
            <ListGroup.Item>JWT Authentication with HttpOnly cookies</ListGroup.Item>
            <ListGroup.Item>Device-based refresh tokens</ListGroup.Item>
            <ListGroup.Item>Responsive Bootstrap layout</ListGroup.Item>
            <ListGroup.Item>Protected and public routes</ListGroup.Item>
          </ListGroup>

          <div className="mt-4">
            <h5>Technology Stack</h5>
            <ul>
              <li>React 18 + TypeScript</li>
              <li>React Router 6</li>
              <li>React Bootstrap 2</li>
              <li>Spring Boot 3</li>
              <li>Gradle</li>
            </ul>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default About;