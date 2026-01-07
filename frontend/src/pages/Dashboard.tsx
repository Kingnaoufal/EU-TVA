import { useQuery } from '@tanstack/react-query';
import {
  Page,
  Layout,
  Card,
  Text,
  BlockStack,
  InlineStack,
  Badge,
  ProgressBar,
  DataTable,
  Banner,
  Spinner,
  Box,
} from '@shopify/polaris';
import { dashboardApi } from '../services/api';
import type { DashboardStats } from '../types';

function Dashboard() {
  // Use 'any' since API returns a different structure than DashboardStats
  const { data: stats, isLoading, error } = useQuery<any>({
    queryKey: ['dashboard'],
    queryFn: async () => {
      const response = await dashboardApi.getStats();
      return response.data;
    },
  });

  if (isLoading) {
    return (
      <Page title="Tableau de bord">
        <Box padding="800">
          <InlineStack align="center">
            <Spinner size="large" />
          </InlineStack>
        </Box>
      </Page>
    );
  }

  if (error) {
    return (
      <Page title="Tableau de bord">
        <Banner tone="critical">
          Une erreur est survenue lors du chargement des données.
        </Banner>
      </Page>
    );
  }

  // Mock data for demo - merge with API data if available
  const mockStats: DashboardStats = {
    totalOrders: stats?.stats?.totalOrders ?? stats?.totalOrders ?? 127,
    ordersWithErrors: stats?.stats?.vatErrors ?? stats?.ordersWithErrors ?? 3,
    errorRate: stats?.stats?.errorRate ? parseFloat(stats.stats.errorRate) : (stats?.errorRate ?? 2.36),
    totalVatCollected: stats?.totalVatCollected ?? 8540.25,
    currentQuarterSales: stats?.currentQuarterSales ?? 42350.00,
    ossThresholdProgress: stats?.ossThreshold?.percentage ?? stats?.ossThresholdProgress ?? 42.35,
    recentAlerts: stats?.alerts?.critical ?? stats?.recentAlerts ?? [
      {
        id: 1,
        type: 'VAT_ERROR',
        severity: 'WARNING',
        title: 'Erreur de taux TVA',
        message: 'Commande #1042 - Taux TVA incorrect pour l\'Allemagne',
        orderId: '1042',
        resolved: false,
        createdAt: new Date().toISOString(),
      },
      {
        id: 2,
        type: 'VIES_FAILURE',
        severity: 'INFO',
        title: 'Validation VIES en attente',
        message: 'Le service VIES était indisponible. Nouvelle tentative prévue.',
        resolved: false,
        createdAt: new Date().toISOString(),
      },
    ],
    vatByCountry: stats?.salesByCountry?.map((s: any) => ({
      country: s.country,
      amount: s.totalVat ?? s.totalSales ?? 0,
      orderCount: s.orderCount ?? 0,
    })) ?? stats?.vatByCountry ?? [
      { country: 'France', amount: 2150.50, orderCount: 45 },
      { country: 'Allemagne', amount: 1890.25, orderCount: 32 },
      { country: 'Belgique', amount: 1250.00, orderCount: 21 },
      { country: 'Espagne', amount: 980.75, orderCount: 15 },
      { country: 'Italie', amount: 750.25, orderCount: 14 },
    ],
  };

  const getSeverityBadge = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
        return <Badge tone="critical">Critique</Badge>;
      case 'WARNING':
        return <Badge tone="warning">Attention</Badge>;
      default:
        return <Badge tone="info">Info</Badge>;
    }
  };

  const vatTableRows = mockStats.vatByCountry.map((item) => [
    item.country,
    item.orderCount.toString(),
    `${item.amount.toFixed(2)} €`,
  ]);

  return (
    <Page
      title="Tableau de bord"
      subtitle="Vue d'ensemble de votre conformité TVA UE"
    >
      <Layout>
        {/* Status Banner */}
        {mockStats.ordersWithErrors === 0 ? (
          <Layout.Section>
            <Banner tone="success">
              ✓ Vous êtes conforme ! Aucune erreur de TVA détectée.
            </Banner>
          </Layout.Section>
        ) : (
          <Layout.Section>
            <Banner tone="warning">
              {mockStats.ordersWithErrors} commande(s) avec des erreurs de TVA nécessitent votre attention.
            </Banner>
          </Layout.Section>
        )}

        {/* Stats Cards */}
        <Layout.Section>
          <InlineStack gap="400" wrap={false}>
            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Commandes UE (ce trimestre)
                  </Text>
                  <Text variant="heading2xl" as="p">
                    {mockStats.totalOrders}
                  </Text>
                </BlockStack>
              </Card>
            </Box>

            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Erreurs de TVA
                  </Text>
                  <InlineStack gap="200" align="start" blockAlign="center">
                    <Text variant="heading2xl" as="p">
                      {mockStats.ordersWithErrors}
                    </Text>
                    <Badge tone={mockStats.ordersWithErrors === 0 ? 'success' : 'warning'}>
                      {`${mockStats.errorRate.toFixed(1)}%`}
                    </Badge>
                  </InlineStack>
                </BlockStack>
              </Card>
            </Box>

            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    TVA collectée (trimestre)
                  </Text>
                  <Text variant="heading2xl" as="p">
                    {mockStats.totalVatCollected.toFixed(2)} €
                  </Text>
                </BlockStack>
              </Card>
            </Box>

            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Ventes UE (trimestre)
                  </Text>
                  <Text variant="heading2xl" as="p">
                    {mockStats.currentQuarterSales.toFixed(2)} €
                  </Text>
                </BlockStack>
              </Card>
            </Box>
          </InlineStack>
        </Layout.Section>

        {/* OSS Threshold Progress */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <InlineStack align="space-between">
                <Text variant="headingMd" as="h2">
                  Seuil OSS (10 000 €)
                </Text>
                <Text variant="bodyMd" as="p">
                  {mockStats.currentQuarterSales.toFixed(2)} € / 10 000 €
                </Text>
              </InlineStack>
              <ProgressBar 
                progress={Math.min(mockStats.ossThresholdProgress * 10, 100)} 
                tone={mockStats.ossThresholdProgress >= 80 ? 'critical' : 'highlight'}
              />
              {mockStats.ossThresholdProgress >= 80 && (
                <Banner tone="warning">
                  Vous approchez du seuil OSS. Préparez-vous à vous inscrire au régime OSS.
                </Banner>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Recent Alerts & VAT by Country */}
        <Layout.Section variant="oneHalf">
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Alertes récentes
              </Text>
              {mockStats.recentAlerts.length === 0 ? (
                <Text as="p" tone="subdued">
                  Aucune alerte pour le moment.
                </Text>
              ) : (
                <BlockStack gap="300">
                  {mockStats.recentAlerts.map((alert) => (
                    <Box 
                      key={alert.id} 
                      padding="300" 
                      background="bg-surface-secondary" 
                      borderRadius="200"
                    >
                      <BlockStack gap="200">
                        <InlineStack align="space-between">
                          <Text variant="bodyMd" as="p" fontWeight="semibold">
                            {alert.title}
                          </Text>
                          {getSeverityBadge(alert.severity)}
                        </InlineStack>
                        <Text variant="bodySm" as="p" tone="subdued">
                          {alert.message}
                        </Text>
                      </BlockStack>
                    </Box>
                  ))}
                </BlockStack>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        <Layout.Section variant="oneHalf">
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                TVA par pays
              </Text>
              <DataTable
                columnContentTypes={['text', 'numeric', 'numeric']}
                headings={['Pays', 'Commandes', 'TVA']}
                rows={vatTableRows}
              />
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Quick Actions */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Actions rapides
              </Text>
              <InlineStack gap="300">
                <a href="/oss-reports" style={{ textDecoration: 'none' }}>
                  <Box 
                    padding="400" 
                    background="bg-surface-secondary" 
                    borderRadius="200"
                    minWidth="200px"
                  >
                    <BlockStack gap="200">
                      <Text variant="headingSm" as="h3">
                        📋 Générer rapport OSS
                      </Text>
                      <Text variant="bodySm" as="p" tone="subdued">
                        Créer le rapport du trimestre en cours
                      </Text>
                    </BlockStack>
                  </Box>
                </a>
                <a href="/vies-validation" style={{ textDecoration: 'none' }}>
                  <Box 
                    padding="400" 
                    background="bg-surface-secondary" 
                    borderRadius="200"
                    minWidth="200px"
                  >
                    <BlockStack gap="200">
                      <Text variant="headingSm" as="h3">
                        ✅ Valider un numéro TVA
                      </Text>
                      <Text variant="bodySm" as="p" tone="subdued">
                        Vérifier via le système VIES
                      </Text>
                    </BlockStack>
                  </Box>
                </a>
                <a href="/vat-analysis" style={{ textDecoration: 'none' }}>
                  <Box 
                    padding="400" 
                    background="bg-surface-secondary" 
                    borderRadius="200"
                    minWidth="200px"
                  >
                    <BlockStack gap="200">
                      <Text variant="headingSm" as="h3">
                        🔍 Analyser les commandes
                      </Text>
                      <Text variant="bodySm" as="p" tone="subdued">
                        Voir le détail des erreurs
                      </Text>
                    </BlockStack>
                  </Box>
                </a>
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>
      </Layout>
    </Page>
  );
}

export default Dashboard;
