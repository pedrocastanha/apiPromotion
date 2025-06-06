import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import { Toaster } from './components/ui/toaster';
import ApiErrorHandler from './components/common/ApiErrorHandler';
import './index.css';
import ReactDOM from 'react-dom/client';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <AuthProvider>
      <ApiErrorHandler>
        <Router>
          <App />
          <Toaster />
        </Router>
      </ApiErrorHandler>
    </AuthProvider>
  </React.StrictMode>
);
