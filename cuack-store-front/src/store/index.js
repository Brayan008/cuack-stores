import { configureStore } from '@reduxjs/toolkit';
import authSlice from './authSlice';
import productsSlice from './productsSlice';
import ordersSlice from './ordersSlice';

const store = configureStore({
  reducer: {
    auth: authSlice,
    products: productsSlice,
    orders: ordersSlice
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false
    }),
});

export default store;