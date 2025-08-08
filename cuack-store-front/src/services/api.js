import axios from 'axios';
import { API_CONFIG } from '../utils/constants';
import store from '../store';
import { logout } from '../store/authSlice';


const api = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const state = store.getState();
    const token = state.auth.token;
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    if (response.data && response.data.data !== undefined) {
      return response.data.data;
    }
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch(logout());
      window.location.href = '/';
      return Promise.reject(new Error('Sesión expirada'));
    }

    if (error.response?.data?.message) {
      return Promise.reject(new Error(error.response.data.message));
    }
    
    if (error.message === 'Network Error') {
      return Promise.reject(new Error('Error de conexión. Verifique su conexión a internet.'));
    }
    
    return Promise.reject(new Error(error.message || 'Error desconocido'));
  }
);

export default api;