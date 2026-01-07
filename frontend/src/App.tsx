import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { Frame, Navigation } from '@shopify/polaris';
import {
  HomeIcon,
  OrderIcon,
  FileIcon,
  AlertTriangleIcon,
  SettingsIcon,
} from '@shopify/polaris-icons';
import { useState, useCallback } from 'react';

import Dashboard from './pages/Dashboard';
import VatAnalysis from './pages/VatAnalysis';
import OssReports from './pages/OssReports';
import Alerts from './pages/Alerts';
import Settings from './pages/Settings';
import ViesValidation from './pages/ViesValidation';

function App() {
  const [mobileNavigationActive, setMobileNavigationActive] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const toggleMobileNavigationActive = useCallback(
    () => setMobileNavigationActive((active: boolean) => !active),
    []
  );

  const navigationMarkup = (
    <Navigation location={location.pathname}>
      <Navigation.Section
        title="EU VAT Ease"
        items={[
          {
            url: '/',
            label: 'Tableau de bord',
            icon: HomeIcon,
            exactMatch: true,
            selected: location.pathname === '/',
            onClick: () => navigate('/'),
          },
          {
            url: '/vat-analysis',
            label: 'Analyse TVA',
            icon: OrderIcon,
            selected: location.pathname === '/vat-analysis',
            onClick: () => navigate('/vat-analysis'),
          },
          {
            url: '/vies-validation',
            label: 'Validation VIES',
            icon: OrderIcon,
            selected: location.pathname === '/vies-validation',
            onClick: () => navigate('/vies-validation'),
          },
          {
            url: '/oss-reports',
            label: 'Rapports OSS',
            icon: FileIcon,
            selected: location.pathname === '/oss-reports',
            onClick: () => navigate('/oss-reports'),
          },
          {
            url: '/alerts',
            label: 'Alertes',
            icon: AlertTriangleIcon,
            badge: '3',
            selected: location.pathname === '/alerts',
            onClick: () => navigate('/alerts'),
          },
          {
            url: '/settings',
            label: 'Paramètres',
            icon: SettingsIcon,
            selected: location.pathname === '/settings',
            onClick: () => navigate('/settings'),
          },
        ]}
      />
    </Navigation>
  );

  return (
    <Frame
      navigation={navigationMarkup}
      showMobileNavigation={mobileNavigationActive}
      onNavigationDismiss={toggleMobileNavigationActive}
    >
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/vat-analysis" element={<VatAnalysis />} />
        <Route path="/vies-validation" element={<ViesValidation />} />
        <Route path="/oss-reports" element={<OssReports />} />
        <Route path="/alerts" element={<Alerts />} />
        <Route path="/settings" element={<Settings />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Frame>
  );
}

export default App;
