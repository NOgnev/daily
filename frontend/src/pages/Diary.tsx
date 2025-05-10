import React, { useState, useEffect, forwardRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Spinner, Alert, Button, Card, Form, Accordion } from 'react-bootstrap';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import '../diary.scss';
import { subDays, addDays } from 'date-fns';

interface ChatMessage {
  id: number;
  side: 'left' | 'right';
  text: string;
}

const ChatPage: React.FC = () => {
  const params = useParams<{ date: string }>();
  const navigate = useNavigate();

  const [selectedDate, setSelectedDate] = useState<Date>(parseDateFromPath(params.date));
  const [chatData, setChatData] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [text, setText] = useState("");

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

  // Загрузка данных (симуляция)
  const fetchChatData = async (date: Date) => {
    try {
      setLoading(true);
      setError(null);

      const data: ChatMessage[] = [
        { id: 1, side: 'left', text: 'Привет!' },
        { id: 2, side: 'right', text: 'Как дела?' },
        { id: 3, side: 'left', text: `Сообщение для ${formatDate(date)}` },
      ];

      await new Promise((res) => setTimeout(res, 500)); // имитация загрузки
      setChatData(data);
    } catch (err: any) {
      setError(err.message || 'Неизвестная ошибка');
      setChatData([]);
    } finally {
      setLoading(false);
    }
  };

  // При загрузке или смене даты
  useEffect(() => {
    fetchChatData(selectedDate);
  }, [selectedDate]);

  // Обработка выбора даты
  const handleDateChange = (date: Date | null) => {
    if (!date) return;
    setSelectedDate(date);
    navigate(`/chat/${formatDate(date)}`);
  };

  // Кастомный компонент для отображения даты как кнопки
  const CustomDateButton = forwardRef<HTMLButtonElement, { value?: string; onClick?: () => void }>(
    ({ value, onClick }, ref) => (
      <Button variant="outline-dark mb-4" onClick={onClick} ref={ref}>
        {value}
      </Button>
    )
  );

  return (
    <Container className="mt-5 fade-in">
      <Card className="shadow-sm">
        <DatePicker
          selected={selectedDate}
          onChange={handleDateChange}
//           highlightDates={[subDays(new Date(), 7), addDays(new Date(), 7)]}
          dateFormat="dd-MM-yyyy"
          withPortal
          locale="ru"
          calendarStartDay={1}
          customInput={<CustomDateButton />}
        />
        <Accordion defaultActiveKey="0">
            <Accordion.Item eventKey="0">
            <Accordion.Header>Обсудим как прошел твой день?</Accordion.Header>
                <Accordion.Body>

                        <Card.Title className="mb-1" >Как прошел твой день?</Card.Title>
                        <Card.Text>
                        Welcome to our secure SPA application with JWT authentication.
                        This demo showcases best practices for React and Spring Boot integration.
                        </Card.Text>

                        <Card.Title className="mb-1">Как дела на работе?</Card.Title>
                        <Card.Text>
                        Welcome to our secure SPA application with JWT authentication.
                        This demo showcases best practices for React and Spring Boot integration.
                        Welcome to our secure SPA application with JWT authentication.
                        This demo showcases best practices for React and Spring Boot integration.
                        </Card.Text>


                        <Card.Title className="mt-3 text-center bg-warning text-white">Подведем итог дня</Card.Title>
                        <Card.Text>
                            Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration. Welcome to our secure SPA application with JWT authentication. This demo showcases best practices for React and Spring Boot integration.
                        </Card.Text>
                </Accordion.Body>
            </Accordion.Item>
            <Accordion.Item eventKey="1">
                <Accordion.Header>Дневная заметка</Accordion.Header>
                <Accordion.Body>
                    <Card.Title>Запиши что-нибудь</Card.Title>
                        <Form>
                            <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                            <Form.Control
                                placeholder="Все отлично..."
                                as="textarea"
                                rows={5}
                                value={text}
                                onChange={(e) => setText(e.target.value)}
                                />
                            </Form.Group>
                            <Button variant="primary" type="submit"  disabled={loading}>
                                {loading ? <Spinner animation="border" size="sm" /> : 'Save'}
                            </Button>
                        </Form>
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>

      {/* Контейнер сообщений
      <Row className="justify-content-center fade-in mt-4">
        <Col>
          <div className="chat-container">
            {loading && (
              <div className="d-flex justify-content-center mb-3 fade-in">
                <Spinner animation="border" />
              </div>
            )}

            {error && <Alert variant="danger">{error}</Alert>}

            {!loading &&
              chatData.map((msg) => (
                <div key={msg.id} className={`fade-in chat-message ${msg.side}`}>
                  <div className={`chat-bubble ${msg.side}`}>
                    {msg.text}
                  </div>
                </div>
              ))}
          </div>
        </Col>
      </Row>*/}
      </Card>
    </Container>
  );
};

export default ChatPage
