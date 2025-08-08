import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { inventoryService } from '../services/inventoryService';

export const fetchProducts = createAsyncThunk(
  'products/fetchProducts',
  async (_, { rejectWithValue }) => {
    try {
      const response = await inventoryService.getAllProducts();
      return response;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const fetchProductByHawa = createAsyncThunk(
  'products/fetchProductByHawa',
  async (hawa, { rejectWithValue }) => {
    try {
      const response = await inventoryService.getProductByHawa(hawa);
      return response;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const checkProductAvailability = createAsyncThunk(
  'products/checkAvailability',
  async (hawa, { rejectWithValue }) => {
    try {
      const response = await inventoryService.checkAvailability(hawa);
      return { hawa, availability: response };
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

const initialState = {
  products: [],
  selectedProduct: null,
  availabilityCheck: {},
  loading: false,
  error: null
};

const productsSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearSelectedProduct: (state) => {
      state.selectedProduct = null;
    },
    clearError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      // Fetch all products
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.products = action.payload;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Fetch product by HAWA
      .addCase(fetchProductByHawa.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductByHawa.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedProduct = action.payload;
      })
      .addCase(fetchProductByHawa.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Check availability
      .addCase(checkProductAvailability.fulfilled, (state, action) => {
        const { hawa, availability } = action.payload;
        state.availabilityCheck[hawa] = availability;
      });
  }
});

export const { clearSelectedProduct, clearError } = productsSlice.actions;
export default productsSlice.reducer;