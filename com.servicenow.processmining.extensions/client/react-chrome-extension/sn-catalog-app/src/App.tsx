import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

import Home from './components/Home';
import Login from './components/Login';

import "./App.css";

function App() {

  return (
    <Router>
      <div className="App">
        <div className="auth-wrapper">
          <div className="auth-inner">
            <Routes>
              <Route path="/" element={<Login />} />
              <Route path="/home" element={<Home />} />
            </Routes>
          </div>
        </div>
      </div>
    </Router>
  )
}

export default App;