import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to requests
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 errors
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt_token');
      window.location.href = '/auth/shopify';
    }
    return Promise.reject(error);
  }
);

export default api;

// Dashboard API
export const dashboardApi = {
  getStats: () => api.get('/vat/dashboard'),
};

// VAT Analysis API
export const vatApi = {
  getOrders: (params?: { 
    page?: number; 
    size?: number; 
    hasErrors?: boolean;
    startDate?: string;
    endDate?: string;
  }) => api.get('/vat/orders', { params }),
  
  getOrderDetail: (orderId: string) => api.get(`/vat/orders/${orderId}`),
  
  analyzeOrder: (orderId: string) => api.post(`/vat/orders/${orderId}/analyze`),
  
  getErrorSummary: () => api.get('/vat/errors/summary'),
};

// VIES Validation API
export const viesApi = {
  validateVatNumber: (vatNumber: string) => 
    api.post('/vat/vies/validate', { vatNumber }),
  
  getValidationHistory: (params?: { page?: number; size?: number }) => 
    api.get('/vat/vies/history', { params }),
  
  retryFailedValidations: () => 
    api.post('/vat/vies/retry-failed'),
};

// OSS Reports API
export const ossApi = {
  getReports: () => api.get('/oss/reports'),
  
  generateReport: (year: number, quarter: number) => 
    api.post('/oss/reports/generate', { year, quarter }),
  
  downloadCsv: (reportId: number) => 
    api.get(`/oss/reports/${reportId}/csv`, { responseType: 'blob' }),
  
  downloadPdf: (reportId: number) => 
    api.get(`/oss/reports/${reportId}/pdf`, { responseType: 'blob' }),
  
  getOssThresholdStatus: () => api.get('/oss/threshold'),
};

// Alerts API
export const alertsApi = {
  getAlerts: (params?: { resolved?: boolean; severity?: string }) => 
    api.get('/vat/alerts', { params }),
  
  resolveAlert: (alertId: number) => 
    api.post(`/vat/alerts/${alertId}/resolve`),
  
  dismissAlert: (alertId: number) => 
    api.post(`/vat/alerts/${alertId}/dismiss`),
};

// Billing API
export const billingApi = {
  getSubscription: () => api.get('/billing/subscription'),
  
  subscribe: (plan: string) => api.post('/billing/subscribe', { plan }),
  
  changePlan: (plan: string) => api.post('/billing/change-plan', { plan }),
  
  cancelSubscription: () => api.post('/billing/cancel'),
};

// Settings API
export const settingsApi = {
  getSettings: () => api.get('/settings'),
  
  updateSettings: (settings: Record<string, unknown>) => 
    api.put('/settings', settings),
  
  getShopInfo: () => api.get('/shop/info'),
};
