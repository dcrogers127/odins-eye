import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import GameManager from './game-manager.jsx';

class AppComponent {
  init = () => {
    this.initLoginRedirecting();
    this.renderComponent();
  };
  initLoginRedirecting = () => {
    axios.interceptors.response.use((response) => {
      return response;
    }, (error) => {
      if (error.response.status === 401) {
        window.location = '/login';
      }
      return Promise.reject(error);
    });
  };
  renderComponent = () => {
    ReactDOM.render(<GameManager />, reactDiv);
  }
}

export default AppComponent;
