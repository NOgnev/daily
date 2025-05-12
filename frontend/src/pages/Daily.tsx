import React, { useEffect, useState, useRef, forwardRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Form, Spinner, Container } from 'react-bootstrap';
import { House, HouseFill, Pencil, PencilFill } from 'react-bootstrap-icons';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

type ItemType = 'question' | 'final';

interface Item {
  id: string;
  title: string;
  body?: string;
  type: ItemType;
}

const MyCard: React.FC<{ item: Item }> = ({ item }) => (
  <Card className="fade-in">
    <Card.Body>
      <Card.Title as="h6">{item.title}</Card.Title>
      {item.body && <Card.Text className="fade-in">{item.body}</Card.Text>}
    </Card.Body>
  </Card>
);

const FinalCard: React.FC<{ item: Item }> = ({ item }) => (
  <Card className="bg-success text-white fade-in">
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
  <div className="bottom-nav d-flex justify-content-around border-top bg-secondary py-2 fixed-bottom slide-in">
    <Button
      variant="link"
      style={{ textDecoration: 'none' }}
      onClick={() => setMode('daily')}
      className="d-flex flex-column align-items-center p-2 mx-2 my-1"
    >
      {mode === 'daily' ? (
        <HouseFill className="fs-3" />
      ) : (
        <House className="fs-3" />
      )}
      <small>Опрос</small>
    </Button>

    <Button
      variant="link"
      style={{ textDecoration: 'none' }}
      onClick={() => setMode('editor')}
      className="d-flex flex-column align-items-center p-2 mx-2 my-1"
    >
      {mode === 'editor' ? (
        <PencilFill className="fs-3" />
      ) : (
        <Pencil className="fs-3" />
      )}
      <small>Редактор</small>
    </Button>
  </div>
);

const Daily: React.FC = () => {
  const params = useParams<{ date: string }>();
  const navigate = useNavigate();

  const [items, setItems] = useState<Item[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [mode, setMode] = useState<'daily' | 'editor'>('daily');
  const [selectedDate, setSelectedDate] = useState<Date>(parseDateFromPath(params.date));

  // Прокрутка до конца страницы
  const endOfPageRef = useRef<HTMLDivElement>(null);

  // Прокрутка страницы вниз, если добавился новый элемент
  useEffect(() => {
    // Прокручиваем вниз только если items изменился
    if (items.length > 0 && endOfPageRef.current) {
      endOfPageRef.current.scrollIntoView({
        behavior: 'smooth',
      });
    }
  }, [items]); // Прокрутка срабатывает при изменении items

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

  // Кастомный компонент для отображения даты как кнопки
  const CustomDateButton = forwardRef<HTMLButtonElement, { value?: string; onClick?: () => void }>(
    ({ value, onClick }, ref) => (
      <Button variant="outline-dark mb-4" onClick={onClick} ref={ref}>
        {value}
      </Button>
    )
  );

  // Обработка выбора даты
  const handleDateChange = (date: Date | null) => {
    if (!date) return;
    setSelectedDate(date);
    navigate(`/chat/${formatDate(date)}`);
  };

  // Форматирование даты: dd-mm-yyyy
  function formatDate(date: Date): string {
    return date.toLocaleDateString('ru-RU').replace(/\./g, '-');
  }

  // Парсинг даты из строки
  function parseDateFromPath(pathDate?: string): Date {
    if (!pathDate) return new Date();
    const [day, month, year] = pathDate.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  useEffect(() => {
    const firstItem: Item = {
      id: '1',
      title: 'Привет! Как тебя зовут?',
      type: 'question',
    };
    setItems([firstItem]);
  }, []);

  return (
    <Container className="mt-3 fade-in" style={{ paddingBottom: '8rem' }}>
      <DatePicker
        selected={selectedDate}
        onChange={handleDateChange}
//         highlightDates={[subDays(new Date(), 7), addDays(new Date(), 7)]}
        dateFormat="dd-MM-yyyy"
        withPortal
        calendarStartDay={1}
        customInput={<CustomDateButton />}
      />

      <Card className="shadow-sm fade-in">
        <Card.Body>
          {mode === 'daily' && (
            <>
              {items.filter(item => item.type !== 'final').map(item => (
                <MyCard key={item.id} item={item} />
              ))}

              {showInput && (
                <>
                  {isLoading ? (
                    <div className="d-flex justify-content-center my-4 fade-in">
                      <Spinner animation="border" role="status" />
                    </div>
                  ) : (
                    <Form className="fade-in" onSubmit={e => { e.preventDefault(); handleSubmit(); }}>
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
                  )}
                </>
              )}

              {lastItem?.type === 'final' && <FinalCard key={lastItem.id} item={lastItem} />}
            </>
          )}

          {mode === 'editor' && <EditorPage />}
        </Card.Body>
      </Card>

      <div ref={endOfPageRef}></div> {/* Место для прокрутки */}
      <BottomNav mode={mode} setMode={setMode} />
    </Container>
  );
};

export default Daily;
