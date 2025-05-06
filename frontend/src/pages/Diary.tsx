import Advice from '../components/Advice';
import { Container } from 'react-bootstrap';

const Diary = () => {
    const cardData = [
        { title: 'Card Title 1', text: 'Some quick example text to build on the card title and make up the bulk of the card\'s content.' },
        { title: 'Card Title 2', text: 'Another example text to build on the card title and make up the bulk of the card\'s content.' },
        { title: 'Card Title 3', text: 'Yet another example text to build on the card title and make up the bulk of the card\'s content.' },
    ];

  return (
    <Container className="mt-5 fade-in">
        <div>
          {cardData.map((card, index) => (
            <Advice key={index} title={card.title} text={card.text} />
          ))}
        </div>
    </Container>
  );
};

export default Diary;