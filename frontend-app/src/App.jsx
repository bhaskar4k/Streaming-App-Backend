/* eslint-disable no-unused-vars */
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './Page-Components/Home/Home.jsx';
import Dashboard from './Page-Components/Dashboard/Dashboard.jsx';
import Login from './Page-Components/Login/Login.jsx';
import Profile from './Page-Components/Profile/Profile.jsx';
import Error from './Error.jsx';
import './App.css';

function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route index element={<Home />} />
          <Route path="/home" element={<Home />} />

          <Route path="/dashboard" element={<Dashboard />} />

          <Route path="/profile" element={<Profile />} />

          <Route path="/login" element={<Login />} />

          <Route path="*" element={<Error />} />

          <Route path="/error" element={<Error />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App;
