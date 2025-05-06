import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import PrivateRoute from './components/PrivateRoute'
import PublicOnlyRoute from './components/PublicOnlyRoute'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import About from './pages/About'
import Profile from './pages/Profile'
import Diary from './pages/Diary'

function App() {
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
                <Route path="diary" element={<Diary />} />
              </Route>

              <Route path="about" element={<About />} />

              <Route path="*" element={<Navigate to="/" replace />} />
           </Route>
        </Routes>
    );
}

export default App;
