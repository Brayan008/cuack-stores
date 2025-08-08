import api from './api';
import { API_CONFIG } from '../utils/constants';

export const orderService = {
    
  createOrder: async (orderData) => {
    try {
      const response = await api.post(API_CONFIG.endpoints.orders, orderData);
      return response;
    } catch (error) {
      throw new Error(`Error creando pedido: ${error.message}`);
    }
  },
  getAllOrders: async (page = 0, size = 20) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.orders}?page=${page}&size=${size}`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo pedidos: ${error.message}`);
    }
  },
  getOrderById: async (orderId) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.orderById}/${orderId}`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo pedido ${orderId}: ${error.message}`);
    }
  },
  updateOrderStatus: async (orderId, statusData) => {
    try {
      const response = await api.put(`${API_CONFIG.endpoints.orderById}/${orderId}/status`, statusData);
      return response;
    } catch (error) {
      throw new Error(`Error actualizando estatus del pedido: ${error.message}`);
    }
  },
  getOrdersByStatus: async (status) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.orders}/status/${status}`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo pedidos por estatus: ${error.message}`);
    }
  },
  searchOrdersByCustomer: async (customerName) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.orders}/search/customer?name=${encodeURIComponent(customerName)}`);
      return response;
    } catch (error) {
      throw new Error(`Error buscando pedidos del cliente: ${error.message}`);
    }
  },
  searchOrdersBySeller: async (sellerName) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.orders}/search/seller?name=${encodeURIComponent(sellerName)}`);
      return response;
    } catch (error) {
      throw new Error(`Error buscando pedidos del vendedor: ${error.message}`);
    }
  },
  getOrderStatistics: async () => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.orders}/stats`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo estadísticas: ${error.message}`);
    }
  }
};