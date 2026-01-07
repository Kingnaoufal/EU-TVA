export interface Shop {
  id: number;
  shopDomain: string;
  shopName: string;
  email: string;
  country: string;
  currency: string;
  ossRegistered: boolean;
  ossCountry?: string;
  subscriptionStatus: 'TRIAL' | 'ACTIVE' | 'CANCELLED' | 'EXPIRED';
  subscriptionPlan?: string;
  trialEndsAt?: string;
  createdAt: string;
}

export interface Order {
  id: number;
  shopifyOrderId: string;
  orderNumber: string;
  customerEmail?: string;
  customerCountry: string;
  totalAmount: number;
  currency?: string;
  taxAmount: number;
  appliedVatRate: number;
  expectedVatRate: number;
  vatDifference?: number | null;
  hasVatError: boolean;
  vatErrorType?: string | null;
  vatExempt: boolean;
  isB2b: boolean;
  customerVatNumber?: string;
  orderDate: string;
}

export interface VatValidation {
  id: number;
  vatNumber: string;
  countryCode: string;
  valid: boolean;
  companyName?: string;
  companyAddress?: string;
  validatedAt: string;
  viesRequestId?: string;
  errorMessage?: string;
}

export interface OssReport {
  id: number;
  year: number;
  quarter: number;
  status: 'DRAFT' | 'GENERATED' | 'SUBMITTED';
  totalSales: number;
  totalVat: number;
  generatedAt: string;
  submittedAt?: string;
  lines: OssReportLine[];
}

export interface OssReportLine {
  id: number;
  countryCode: string;
  countryName: string;
  vatRate: number;
  taxableAmount: number;
  vatAmount: number;
  orderCount: number;
}

export interface VatAlert {
  id: number;
  type: 'OSS_THRESHOLD_WARNING' | 'OSS_THRESHOLD_EXCEEDED' | 'VAT_RATE_ERROR' | 'VAT_MISSING' | 'SHOPIFY_CONFIG_ERROR' | 'QUARTERLY_REMINDER' | 'SUBSCRIPTION_EXPIRING' | 'B2B_VAT_INVALID' | 'ORDER_ERROR';
  severity: 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL';
  title: string;
  message: string;
  orderId?: string;
  resolved: boolean;
  resolvedAt?: string;
  createdAt: string;
}

export interface DashboardStats {
  totalOrders: number;
  ordersWithErrors: number;
  errorRate: number;
  totalVatCollected: number;
  currentQuarterSales: number;
  ossThresholdProgress: number;
  recentAlerts: VatAlert[];
  vatByCountry: {
    country: string;
    amount: number;
    orderCount: number;
  }[];
}

export interface Subscription {
  status: 'TRIAL' | 'ACTIVE' | 'CANCELLED' | 'EXPIRED';
  plan?: string;
  price?: number;
  billingCycle?: string;
  trialEndsAt?: string;
  nextBillingAt?: string;
}
