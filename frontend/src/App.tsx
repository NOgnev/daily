import React, { useEffect } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { Spinner, Container } from 'react-bootstrap';
import logo from './logo.svg';
import './App.css';
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import PrivateRoute from './components/PrivateRoute'
import PublicOnlyRoute from './components/PublicOnlyRoute'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import About from './pages/About'
import Profile from './pages/Profile'
import { generateDeviceId } from './utils/deviceUtils'

function App() {
  const { checkAuth, isLoading } = useAuth();

    useEffect(() => {
        generateDeviceId();
        checkAuth();
    }, [checkAuth]);

    if (isLoading) return (
        <Container className="d-flex justify-content-center mt-5">
            <Spinner animation="border" />
        </Container>
    );

    return (
        <Routes>
           <Route path="/" element={<Layout />}>

              <Route element={<PublicOnlyRoute />}>
                <Route index element={<Login />} />
                <Route path="login" element={<Login />} />
                <Route path="register" element={<Register />} />
              </Route>

              <Route element={<PrivateRoute />}>
                <Route path="profile" element={<Profile />} />
              </Route>

              <Route path="about" element={<About />} />

              <Route path="*" element={<Navigate to="/" replace />} />
           </Route>
        </Routes>
    );
}

export default App;
