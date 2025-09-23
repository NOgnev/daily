import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import './index.scss';
import App from './App';
import 'react-bootstrap/dist/react-bootstrap.min.js'
import './index.scss';
import reportWebVitals from './reportWebVitals';
import './i18n';
import './yupLocale';

console.log(`%c

      dP MMP"""""""MM M""M dP
      88 M\' .mmmm  MM M  M 88
.d888b88 M         \`M M  M 88 dP    dP
88'  \`88 M  MMMMM  MM M  M 88 88    88
88.  .88 M  MMMMM  MM M  M 88 88.  .88
\`88888P8 M  MMMMM  MM M  M dP \`8888P88
         MMMMMMMMMMMM MMMM         .88
                               d8888P
`, 'color: white; font-family: monospace; font-size: 12px;');
console.log('%cWelcome to dAIly v1.0.0', 'color: white; font-size: 14px;');

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <AuthProvider>
        <BrowserRouter>
            <App />
        </BrowserRouter>
    </AuthProvider>
  </React.StrictMode>
);

reportWebVitals();
