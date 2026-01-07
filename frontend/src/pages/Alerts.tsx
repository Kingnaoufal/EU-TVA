import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Page,
  Layout,
  Card,
  Text,
  BlockStack,
  InlineStack,
  Button,
  Badge,
  Banner,
  Spinner,
  Box,
  Tabs,
} from '@shopify/polaris';
import { useState } from 'react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { alertsApi } from '../services/api';
import type { VatAlert } from '../types';

function Alerts() {
  const [selectedTab, setSelectedTab] = useState(0);
  const queryClient = useQueryClient();

  const { data: alertsData, isLoading } = useQuery<{ content: VatAlert[]; unreadCount: number }>({
    queryKey: ['alerts'],
    queryFn: async () => {
      const response = await alertsApi.getAlerts();
      return response.data;
    },
  });

  const resolveMutation = useMutation({
    mutationFn: async (alertId: number) => {
      const response = await alertsApi.resolveAlert(alertId);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });

  const dismissMutation = useMutation({
    mutationFn: async (alertId: number) => {
      const response = await alertsApi.dismissAlert(alertId);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });

  const alerts = alertsData?.content || [];
  const activeAlerts = alerts.filter((a: VatAlert) => !a.resolved);
  const resolvedAlerts = alerts.filter((a: VatAlert) => a.resolved);

  const tabs = [
    {
      id: 'active',
      content: `Actives (${activeAlerts.length})`,
      panelID: 'active-alerts',
    },
    {
      id: 'resolved',
      content: `Résolues (${resolvedAlerts.length})`,
      panelID: 'resolved-alerts',
    },
  ];

  const getSeverityTone = (severity: string): 'critical' | 'warning' | 'info' => {
    switch (severity) {
      case 'CRITICAL':
        return 'critical';
      case 'WARNING':
        return 'warning';
      default:
        return 'info';
    }
  };

  const getSeverityLabel = (severity: string): string => {
    switch (severity) {
      case 'CRITICAL':
        return 'Critique';
      case 'WARNING':
        return 'Attention';
      default:
        return 'Info';
    }
  };

  const getTypeIcon = (type: string): string => {
    switch (type) {
      case 'VAT_ERROR':
        return '⚠️';
      case 'B2B_VAT_CHARGED':
        return '💼';
      case 'OSS_THRESHOLD':
        return '📊';
      case 'OSS_DEADLINE':
        return '📅';
      case 'VIES_FAILURE':
        return '🔄';
      case 'RATE_CHANGE':
        return '📈';
      default:
        return '🔔';
    }
  };

  const displayedAlerts = selectedTab === 0 ? activeAlerts : resolvedAlerts;

  if (isLoading) {
    return (
      <Page title="Alertes">
        <Box padding="800">
          <InlineStack align="center">
            <Spinner size="large" />
          </InlineStack>
        </Box>
      </Page>
    );
  }

  return (
    <Page
      title="Alertes"
      subtitle="Notifications et anomalies détectées"
    >
      <Layout>
        {/* Summary */}
        <Layout.Section>
          <InlineStack gap="400">
            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Alertes actives
                  </Text>
                  <Text variant="heading2xl" as="p">
                    {activeAlerts.length}
                  </Text>
                </BlockStack>
              </Card>
            </Box>
            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Critiques
                  </Text>
                  <Text variant="heading2xl" as="p" tone="critical">
                    {activeAlerts.filter((a) => a.severity === 'CRITICAL').length}
                  </Text>
                </BlockStack>
              </Card>
            </Box>
            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Avertissements
                  </Text>
                  <Text variant="heading2xl" as="p" tone="caution">
                    {activeAlerts.filter((a) => a.severity === 'WARNING').length}
                  </Text>
                </BlockStack>
              </Card>
            </Box>
            <Box minWidth="200px" width="25%">
              <Card>
                <BlockStack gap="200">
                  <Text variant="bodyMd" as="p" tone="subdued">
                    Informations
                  </Text>
                  <Text variant="heading2xl" as="p">
                    {activeAlerts.filter((a) => a.severity === 'INFO').length}
                  </Text>
                </BlockStack>
              </Card>
            </Box>
          </InlineStack>
        </Layout.Section>

        {/* Alerts List */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Tabs tabs={tabs} selected={selectedTab} onSelect={setSelectedTab} />

              {displayedAlerts.length === 0 ? (
                <Box padding="400">
                  <Banner tone="success">
                    {selectedTab === 0
                      ? '🎉 Aucune alerte active ! Votre conformité TVA est excellente.'
                      : 'Aucune alerte résolue pour le moment.'}
                  </Banner>
                </Box>
              ) : (
                <BlockStack gap="300">
                  {displayedAlerts.map((alert) => (
                    <Box
                      key={alert.id}
                      padding="400"
                      background="bg-surface-secondary"
                      borderRadius="200"
                      borderColor={
                        alert.severity === 'CRITICAL'
                          ? 'border-critical'
                          : alert.severity === 'WARNING'
                          ? 'border-caution'
                          : 'border'
                      }
                      borderWidth="025"
                    >
                      <BlockStack gap="300">
                        <InlineStack align="space-between" blockAlign="start">
                          <InlineStack gap="300" blockAlign="center">
                            <Text variant="headingLg" as="span">
                              {getTypeIcon(alert.type)}
                            </Text>
                            <BlockStack gap="100">
                              <InlineStack gap="200" blockAlign="center">
                                <Text variant="headingSm" as="h3">
                                  {alert.title}
                                </Text>
                                <Badge tone={getSeverityTone(alert.severity)}>
                                  {getSeverityLabel(alert.severity)}
                                </Badge>
                              </InlineStack>
                              <Text variant="bodySm" as="p" tone="subdued">
                                {format(new Date(alert.createdAt), "dd MMMM yyyy 'à' HH:mm", {
                                  locale: fr,
                                })}
                              </Text>
                            </BlockStack>
                          </InlineStack>

                          {!alert.resolved && (
                            <InlineStack gap="200">
                              <Button
                                variant="primary"
                                size="slim"
                                onClick={() => resolveMutation.mutate(alert.id)}
                                loading={resolveMutation.isPending}
                              >
                                Résoudre
                              </Button>
                              <Button
                                variant="plain"
                                size="slim"
                                onClick={() => dismissMutation.mutate(alert.id)}
                              >
                                Ignorer
                              </Button>
                            </InlineStack>
                          )}
                        </InlineStack>

                        <Text as="p">{alert.message}</Text>

                        {alert.orderId && (
                          <InlineStack>
                            <a href={`/vat-analysis?order=${alert.orderId}`}>
                              <Button variant="plain" size="slim">
                                Voir la commande #{alert.orderId}
                              </Button>
                            </a>
                          </InlineStack>
                        )}

                        {alert.resolved && alert.resolvedAt && (
                          <Text variant="bodySm" as="p" tone="success">
                            ✓ Résolu le{' '}
                            {format(new Date(alert.resolvedAt), "dd MMMM yyyy 'à' HH:mm", {
                              locale: fr,
                            })}
                          </Text>
                        )}
                      </BlockStack>
                    </Box>
                  ))}
                </BlockStack>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Alert Types Info */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Types d'alertes
              </Text>
              <InlineStack gap="400" wrap>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="bodyMd" as="p" fontWeight="semibold">
                      ⚠️ Erreur TVA
                    </Text>
                    <Text variant="bodySm" as="p" tone="subdued">
                      Taux incorrect ou TVA manquante
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="bodyMd" as="p" fontWeight="semibold">
                      💼 B2B avec TVA
                    </Text>
                    <Text variant="bodySm" as="p" tone="subdued">
                      TVA facturée sur vente B2B
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="bodyMd" as="p" fontWeight="semibold">
                      📊 Seuil OSS
                    </Text>
                    <Text variant="bodySm" as="p" tone="subdued">
                      Approche du seuil 10 000€
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="bodyMd" as="p" fontWeight="semibold">
                      📅 Échéance OSS
                    </Text>
                    <Text variant="bodySm" as="p" tone="subdued">
                      Date limite de déclaration
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="bodyMd" as="p" fontWeight="semibold">
                      🔄 VIES
                    </Text>
                    <Text variant="bodySm" as="p" tone="subdued">
                      Problème de validation TVA
                    </Text>
                  </BlockStack>
                </Box>
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>
      </Layout>
    </Page>
  );
}

export default Alerts;
