import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { orderService } from '../services/orderService';

export const createOrder = createAsyncThunk(
    'orders/createOrder',
    async (orderData, { rejectWithValue }) => {
        try {
            const response = await orderService.createOrder(orderData);
            return response;
        } catch (error) {
            return rejectWithValue(error.message);
        }
    }
);

export const fetchOrders = createAsyncThunk(
    'orders/fetchOrders',
    async ({ page = 0, size = 20 } = {}, { rejectWithValue }) => {
        try {
            const response = await orderService.getAllOrders(page, size);
            return response;
        } catch (error) {
            return rejectWithValue(error.message);
        }
    }
);

export const fetchOrderById = createAsyncThunk(
    'orders/fetchOrderById',
    async (orderId, { rejectWithValue }) => {
        try {
            const response = await orderService.getOrderById(orderId);
            return response;
        } catch (error) {
            return rejectWithValue(error.message);
        }
    }
);

export const updateOrderStatus = createAsyncThunk(
    'orders/updateStatus',
    async ({ orderId, status, reason }, { rejectWithValue }) => {
        try {
            const response = await orderService.updateOrderStatus(orderId, { status, reason });
            return response;
        } catch (error) {
            return rejectWithValue(error.message);
        }
    }
);

const initialState = {
    orders: [],
    selectedOrder: null,
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    loading: false,
    error: null,
    createOrderLoading: false,
    createOrderSuccess: false
};

const ordersSlice = createSlice({
    name: 'orders',
    initialState,
    reducers: {
        clearSelectedOrder: (state) => {
            state.selectedOrder = null;
        },
        clearError: (state) => {
            state.error = null;
        },
        clearCreateOrderSuccess: (state) => {
            state.createOrderSuccess = false;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(createOrder.pending, (state) => {
                state.createOrderLoading = true;
                state.error = null;
                state.createOrderSuccess = false;
            })
            .addCase(createOrder.fulfilled, (state, action) => {
                state.createOrderLoading = false;
                state.createOrderSuccess = true;
                state.selectedOrder = action.payload;
                state.orders.unshift(action.payload);
            })
            .addCase(createOrder.rejected, (state, action) => {
                state.createOrderLoading = false;
                state.error = action.payload;
            })
            .addCase(fetchOrders.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchOrders.fulfilled, (state, action) => {
                state.loading = false;
                state.orders = action.payload.content || action.payload;
                state.totalElements = action.payload.totalElements || action.payload.length;
                state.totalPages = action.payload.totalPages || 1;
                state.currentPage = action.payload.number || 0;
            })
            .addCase(fetchOrders.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload;
            })
            .addCase(fetchOrderById.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchOrderById.fulfilled, (state, action) => {
                state.loading = false;
                state.selectedOrder = action.payload;
            })
            .addCase(fetchOrderById.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload;
            })
            .addCase(updateOrderStatus.fulfilled, (state, action) => {
                const updatedOrder = action.payload;
                const index = state.orders.findIndex(order => order.id === updatedOrder.id);
                if (index !== -1) {
                    state.orders[index] = updatedOrder;
                }
                if (state.selectedOrder && state.selectedOrder.id === updatedOrder.id) {
                    state.selectedOrder = updatedOrder;
                }
            });
    }
});

export const { clearSelectedOrder, clearError, clearCreateOrderSuccess } = ordersSlice.actions;
export default ordersSlice.reducer;