import Card from 'react-bootstrap/Card';
import React from 'react';

interface AdviceProps {
  title: string;
  text: string;
}

const Advice: React.FC<AdviceProps> = ({ title, text }) => {
  return (
    <Card bg="light" border="success" className="fade-in mb-2 shadow-sm">
      <Card.Body>
        <Card.Title>{title}</Card.Title>
        <Card.Text>
          {text}
        </Card.Text>
      </Card.Body>
    </Card>
  );
};

export default Advice;