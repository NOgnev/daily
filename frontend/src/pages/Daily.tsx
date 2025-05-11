import React, { useEffect, useState } from 'react';
import { Card, Button, Form, Spinner, Container } from 'react-bootstrap';
import { HouseFill, PencilSquare } from 'react-bootstrap-icons';

type ItemType = 'question' | 'final';

interface Item {
  id: string;
  title: string;
  body?: string;
  type: ItemType;
}

const MyCard: React.FC<{ item: Item }> = ({ item }) => (
  <Card className="mb-3 fade-in">
    <Card.Body>
      <Card.Title>{item.title}</Card.Title>
      {item.body && <Card.Text className="fade-in">{item.body}</Card.Text>}
    </Card.Body>
  </Card>
);

const FinalCard: React.FC<{ item: Item }> = ({ item }) => (
  <Card className="mt-4 bg-success text-white fade-in">
    <Card.Body>
      <Card.Title>{item.title}</Card.Title>
      <Card.Text>{item.body}</Card.Text>
    </Card.Body>
  </Card>
);

const fetchNextStep = async (input: string): Promise<Item[]> => {
  await new Promise(r => setTimeout(r, 500));
  step++;
  if (step === 2) {
    return [{ id: '2', title: 'Сколько тебе лет?', type: 'question' }];
  }
  if (step === 3) {
    return [{ id: '3', title: 'Какой у тебя любимый цвет?', type: 'question' }];
  }
  return [{ id: 'final', title: 'Это всё!', body: 'Спасибо за ответы.', type: 'final' }];
};
let step = 1;

const EditorPage: React.FC = () => {
  const [text, setText] = useState('Это старый текст');
  const [isEditing, setIsEditing] = useState(false);

  return (
    <Card className="fade-in">
      <Card.Body>
        <Card.Title className="mb-3">Редактор</Card.Title>
        {isEditing ? (
          <>
            <Form.Control
              as="textarea"
              rows={3}
              value={text}
              onChange={(e) => setText(e.target.value)}
            />
            <Button className="mt-2" onClick={() => setIsEditing(false)}>Save</Button>
          </>
        ) : (
          <>
            <Card.Text>{text}</Card.Text>
            <Button onClick={() => setIsEditing(true)}>Edit</Button>
          </>
        )}
      </Card.Body>
    </Card>
  );
};

const BottomNav: React.FC<{ mode: string; setMode: (m: 'daily' | 'editor') => void }> = ({ mode, setMode }) => (
  <div className="bottom-nav d-flex justify-content-around border-top bg-light py-2 fixed-bottom fade-in">
    <Button
      variant={mode === 'daily' ? 'primary' : 'light'}
      onClick={() => setMode('daily')}
      className="d-flex flex-column align-items-center"
    >
      <HouseFill />
      <small>Опрос</small>
    </Button>
    <Button
      variant={mode === 'editor' ? 'primary' : 'light'}
      onClick={() => setMode('editor')}
      className="d-flex flex-column align-items-center"
    >
      <PencilSquare />
      <small>Редактор</small>
    </Button>
  </div>
);

const Daily: React.FC = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [mode, setMode] = useState<'daily' | 'editor'>('daily');

  useEffect(() => {
    const firstItem: Item = {
      id: '1',
      title: 'Привет! Как тебя зовут?',
      type: 'question',
    };
    setItems([firstItem]);
  }, []);

  const handleSubmit = async () => {
    if (!inputValue.trim()) return;
    setIsLoading(true);
    const response = await fetchNextStep(inputValue);
    setItems(prev => {
      const last = prev[prev.length - 1];
      const updatedLast = { ...last, body: inputValue };
      return [...prev.slice(0, -1), updatedLast, ...response];
    });
    setInputValue('');
    setIsLoading(false);
  };

  const lastItem = items[items.length - 1];
  const showInput = lastItem?.type === 'question';

  return (
    <Container className="mt-5 pb-5"> {/* убран mt-3 для выравнивания с другими страницами */}
      <Card className="shadow-sm fade-in">
        <Card.Body>
          {mode === 'daily' && (
            <>
              {items.filter(item => item.type !== 'final').map(item => (
                <MyCard key={item.id} item={item} />
              ))}

              {showInput && (
                isLoading ? (
                  <div className="d-flex justify-content-center my-4 fade-in">
                    <Spinner animation="border" role="status" />
                  </div>
                ) : (
                  <Form className="mt-4 fade-in" onSubmit={e => { e.preventDefault(); handleSubmit(); }}>
                    <Form.Group controlId="userInput">
                      <Form.Control
                        type="text"
                        placeholder="Введите ответ"
                        value={inputValue}
                        onChange={e => setInputValue(e.target.value)}
                      />
                    </Form.Group>
                    <Button className="mt-2 fade-in" onClick={handleSubmit}>
                      Отправить
                    </Button>
                  </Form>
                )
              )}

              {lastItem?.type === 'final' && <FinalCard key={lastItem.id} item={lastItem} />}
            </>
          )}

          {mode === 'editor' && <EditorPage />}
        </Card.Body>
      </Card>

      <BottomNav mode={mode} setMode={setMode} />
    </Container>
  );
};

export default Daily;
