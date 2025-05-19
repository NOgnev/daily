import React, { useEffect, useState, useRef, forwardRef } from 'react';
import { Card, Button, Form, Spinner, Container } from 'react-bootstrap';
import { House, HouseFill, Pencil, PencilFill } from 'react-bootstrap-icons';
import { dailyApi, DialogItem, DialogItemType } from '../api/dailyApi';
import { handleApiError } from '../utils/handleApiError';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import '../daily.scss';

const MyCard: React.FC<{ item: DialogItem }> = ({ item }) => (
  <Card className="fade-in">
    <Card.Body>
      {item.type === 'question' && <Card.Title className="fade-in" as="h6">{item.content}</Card.Title>}
      {item.type === 'answer' && <Card.Text className="fade-in">{item.content}</Card.Text>}
    </Card.Body>
  </Card>
);

const FinalCard: React.FC<{ item: DialogItem }> = ({ item }) => (
  <Card className="bg-success text-white fade-in">
    <Card.Body>
      <Card.Title>Диалог завершен</Card.Title>
      <Card.Text>{item.content}</Card.Text>
    </Card.Body>
  </Card>
);

const EditorPage: React.FC = () => {
  const [text, setText] = useState('Запиши события этого дня');
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
  <div className="bottom-nav d-flex justify-content-around border-top bg-secondary fixed-bottom slide-in">
    <Button
      variant="link"
      style={{ color: mode === 'daily' ? 'white' : 'silver', textDecoration: 'none' }}
      onClick={() => setMode('daily')}
      className="d-flex flex-column align-items-center p-2 mx-2 my-1"
    >
      {mode === 'daily' ? (
        <HouseFill className="fs-3" />
      ) : (
        <House className="fs-3" />
      )}
      <span style={{ fontSize: '0.8em' }}>Опрос</span>
    </Button>

    <Button
      variant="link"
      style={{ color: mode === 'editor' ? 'white' : 'silver', textDecoration: 'none' }}
      onClick={() => setMode('editor')}
      className="d-flex flex-column align-items-center p-2 mx-2 my-1"
    >
      {mode === 'editor' ? (
        <PencilFill className="fs-3" />
      ) : (
        <Pencil className="fs-3" />
      )}
      <span style={{ fontSize: '0.8em' }}>Редактор</span>
    </Button>
  </div>
);

const Daily: React.FC = () => {
  const [items, setItems] = useState<DialogItem[]>([]); // Инициализация пустым массивом
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isMounting, setIsMounting] = useState(true);
  const [mode, setMode] = useState<'daily' | 'editor'>('daily');
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [error, setError] = useState<string | null>(null);

  const fetchNextStep = async (date: Date, input: string | null): Promise<DialogItem[]> => {
      try {
          return await dailyApi.next(date, input);
      } catch (err) {
          handleApiError(err, setError);
          return [];
      }
  };

  const handleSubmit = async () => {
    if (!inputValue.trim()) return;
    setIsLoading(true);
    const response = await fetchNextStep(selectedDate, inputValue);

    setItems(prev => {
      // Извлекаем только новые элементы, игнорируя уже добавленные
      const existingIds = new Set(prev.map(item => item.id));
      const newItems = response.filter(item => !existingIds.has(item.id));
      return [...prev, ...newItems]; // Добавляем только новые элементы
    });
    setInputValue('');
    setIsLoading(false);
  };

  const handleSubmitStart = async () => {
    setIsLoading(true);
    const response = await fetchNextStep(selectedDate, null);
    setItems(response); // Загружаем все элементы сразу
    setIsLoading(false);
  };

  const lastItem = items?.[items.length - 1];
  const showInput = lastItem?.type === 'question';

  const CustomDateButton = forwardRef<HTMLButtonElement, { value?: string; onClick?: () => void }>(
    ({ value, onClick }, ref) => (
      <Button variant="outline-dark mb-4" onClick={onClick} ref={ref}>
        {value}
      </Button>
    )
  );

  const handleDateChange = (date: Date | null) => {
    if (!date) return;
    setSelectedDate(date);
    setItems([]);
  };

  useEffect(() => {
    const fetchData = async () => {
      if (!items || items.length === 0) {
        setIsLoading(true);
        try {
            const dialog = await dailyApi.getDialog(selectedDate);
            setItems(dialog);
        } catch (err) {
            handleApiError(err, setError);
        } finally {
            setIsLoading(false);
            setIsMounting(false);
        }
      }
    };
    fetchData();
  }, [selectedDate]);

  return (
    <div>
        {isMounting ? (
            <div className="d-flex justify-content-center my-4">
              <Spinner animation="border" role="status" />
            </div>
        ) : (
            <Container className="mt-3 fade-in" style={{ paddingBottom: '8rem' }}>
                <DatePicker
                    selected={selectedDate}
                    onChange={handleDateChange}
                    dateFormat="dd-MM-yyyy"
                    withPortal
                    calendarStartDay={1}
                    customInput={<CustomDateButton />}
                />

                <Card className="shadow-sm fade-in">
                    <Card.Body>
                        {mode === 'daily' && (!items || items.length === 0) && (
                            <>
                                {isLoading ? (
                                    <div className="d-flex justify-content-center my-4 fade-in">
                                        <Spinner animation="border" role="status" />
                                    </div>
                                ) : (
                                    <div className="text-left fade-in">
                                        <p>Ответьте на пару вопросов о событиях дня</p>
                                        <Button onClick={handleSubmitStart}>Начать</Button>
                                    </div>
                                )}
                            </>
                        )}

                        {mode === 'daily' && (items && items.length > 0) && (
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
                                                <Button className="mt-2" onClick={handleSubmit}>Отправить</Button>
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
            </Container>
        )}
        <BottomNav mode={mode} setMode={setMode} />
    </div>
  );
};

export default Daily;
