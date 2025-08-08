import api from './api';
import { API_CONFIG } from '../utils/constants';

export const inventoryService = {

  getAllProducts: async () => {
    try {
      const response = await api.get(API_CONFIG.endpoints.products);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo productos: ${error.message}`);
    }
  },


  getProductByHawa: async (hawa) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.productByHawa}/${hawa}`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo producto ${hawa}: ${error.message}`);
    }
  },


  checkAvailability: async (hawa) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.productByHawa}/${hawa}/availability`);
      return response;
    } catch (error) {
      throw new Error(`Error verificando disponibilidad de ${hawa}: ${error.message}`);
    }
  },


  getAvailableProducts: async () => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.products}/available`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo productos disponibles: ${error.message}`);
    }
  },

  
  getProductsWithLowStock: async (threshold = 5) => {
    try {
      const response = await api.get(`${API_CONFIG.endpoints.products}/low-stock?threshold=${threshold}`);
      return response;
    } catch (error) {
      throw new Error(`Error obteniendo productos con stock bajo: ${error.message}`);
    }
  }
};