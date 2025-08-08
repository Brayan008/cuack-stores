export const AUTH0_CONFIG = {
  clientId: 'ikWPpY8zw0AkwBUhsKj6rRyqdX2m7Isi',
  domain: 'https://dev-9pn-820c.us.auth0.com',
  authorizationEndpoint: 'https://dev-9pn-820c.us.auth0.com/authorize',
  redirectUri: window.location.origin,
  scopes: ['openid', 'profile', 'email']
};

export const API_CONFIG = {
  // baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  baseURL: 'http://localhost:8080',
  endpoints: {
    products: '/api/v1/inventory/products',
    productByHawa: '/api/v1/inventory/product',
    orders: '/api/v1/orders',
    orderById: '/api/v1/orders'
  }
};

export const ORDER_STATUS = {
  PENDIENTE: 'PENDIENTE',
  ENTREGADO: 'ENTREGADO',
  CANCELADO: 'CANCELADO'
};

export const ORDER_STATUS_LABELS = {
  [ORDER_STATUS.PENDIENTE]: 'Pendiente',
  [ORDER_STATUS.ENTREGADO]: 'Entregado',
  [ORDER_STATUS.CANCELADO]: 'Cancelado'
};

export const ORDER_STATUS_VARIANTS = {
  [ORDER_STATUS.PENDIENTE]: 'warning',
  [ORDER_STATUS.ENTREGADO]: 'success',
  [ORDER_STATUS.CANCELADO]: 'danger'
};