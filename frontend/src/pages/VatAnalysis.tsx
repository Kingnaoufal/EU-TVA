import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Page,
  Layout,
  Card,
  Text,
  BlockStack,
  InlineStack,
  Badge,
  DataTable,
  Filters,
  ChoiceList,
  Button,
  Pagination,
  Modal,
  Banner,
  Spinner,
  Box,
} from '@shopify/polaris';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { vatApi } from '../services/api';
import type { Order } from '../types';

function VatAnalysis() {
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [modalActive, setModalActive] = useState(false);
  const [filters, setFilters] = useState({
    hasErrors: undefined as boolean | undefined,
    searchQuery: '',
  });
  const [page, setPage] = useState(1);

  const { data, isLoading, error } = useQuery({
    queryKey: ['orders', page, filters],
    queryFn: async () => {
      const response = await vatApi.getOrders({
        page: page - 1,
        size: 20,
        hasErrors: filters.hasErrors,
      });
      return response.data;
    },
  });

  const orders = data?.content || [];
  const totalPages = data?.totalPages || 1;

  const getCountryName = (code: string): string => {
    const countries: Record<string, string> = {
      DE: 'Allemagne',
      FR: 'France',
      BE: 'Belgique',
      NL: 'Pays-Bas',
      ES: 'Espagne',
      IT: 'Italie',
      AT: 'Autriche',
      PT: 'Portugal',
      PL: 'Pologne',
      SE: 'Suède',
      DK: 'Danemark',
      FI: 'Finlande',
      IE: 'Irlande',
      LU: 'Luxembourg',
      CZ: 'République tchèque',
      GR: 'Grèce',
      HU: 'Hongrie',
      RO: 'Roumanie',
      BG: 'Bulgarie',
      SK: 'Slovaquie',
      SI: 'Slovénie',
      HR: 'Croatie',
      LT: 'Lituanie',
      LV: 'Lettonie',
      EE: 'Estonie',
      CY: 'Chypre',
      MT: 'Malte',
    };
    return countries[code] || code;
  };

  const handleOrderClick = (order: Order) => {
    setSelectedOrder(order);
    setModalActive(true);
  };

  const tableRows = orders.map((order: Order) => [
    <Button variant="plain" onClick={() => handleOrderClick(order)}>
      {order.orderNumber}
    </Button>,
    format(new Date(order.orderDate), 'dd MMM yyyy', { locale: fr }),
    getCountryName(order.customerCountry),
    `${order.totalAmount?.toFixed(2) ?? '0.00'} EUR`,
    `${order.appliedVatRate?.toFixed(2) ?? '0.00'}%`,
    order.isB2b ? <Badge tone="info">B2B</Badge> : <Badge>B2C</Badge>,
    order.hasVatError ? (
      <Badge tone="critical">Erreur</Badge>
    ) : (
      <Badge tone="success">OK</Badge>
    ),
  ]);

  if (isLoading) {
    return (
      <Page title="Analyse TVA">
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
      <Page title="Analyse TVA">
        <Banner tone="critical">
          Une erreur est survenue lors du chargement des commandes.
        </Banner>
      </Page>
    );
  }

  return (
    <Page
      title="Analyse TVA"
      subtitle="Vérifiez la conformité de vos commandes UE"
    >
      <Layout>
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Filters
                queryValue={filters.searchQuery}
                queryPlaceholder="Rechercher par numéro de commande..."
                onQueryChange={(value) => setFilters({ ...filters, searchQuery: value })}
                onQueryClear={() => setFilters({ ...filters, searchQuery: '' })}
                filters={[
                  {
                    key: 'hasErrors',
                    label: 'Statut',
                    filter: (
                      <ChoiceList
                        title="Statut"
                        titleHidden
                        choices={[
                          { label: 'Toutes', value: 'all' },
                          { label: 'Avec erreurs', value: 'errors' },
                          { label: 'Sans erreurs', value: 'ok' },
                        ]}
                        selected={[
                          filters.hasErrors === undefined 
                            ? 'all' 
                            : filters.hasErrors ? 'errors' : 'ok'
                        ]}
                        onChange={(selected: string[]) => {
                          const value = selected[0];
                          setFilters({
                            ...filters,
                            hasErrors: value === 'all' 
                              ? undefined 
                              : value === 'errors',
                          });
                        }}
                      />
                    ),
                    shortcut: true,
                  },
                ]}
                onClearAll={() => setFilters({ hasErrors: undefined, searchQuery: '' })}
              />

              <DataTable
                columnContentTypes={['text', 'text', 'text', 'numeric', 'numeric', 'text', 'text']}
                headings={['Commande', 'Date', 'Pays', 'Total', 'TVA', 'Type', 'Statut']}
                rows={tableRows}
              />

              <InlineStack align="center">
                <Pagination
                  hasPrevious={page > 1}
                  hasNext={page < totalPages}
                  onPrevious={() => setPage(page - 1)}
                  onNext={() => setPage(page + 1)}
                />
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>
      </Layout>

      {/* Order Detail Modal */}
      <Modal
        open={modalActive}
        onClose={() => setModalActive(false)}
        title={`Détail de la commande ${selectedOrder?.orderNumber}`}
        primaryAction={{
          content: 'Fermer',
          onAction: () => setModalActive(false),
        }}
      >
        <Modal.Section>
          {selectedOrder && (
            <BlockStack gap="400">
              {selectedOrder.hasVatError && (
                <Banner tone="critical">
                  <Text as="p" fontWeight="semibold">
                    {selectedOrder.vatErrorType?.replace(/_/g, ' ')}
                  </Text>
                  {selectedOrder.vatDifference && (
                    <Text as="p">Différence de TVA: {selectedOrder.vatDifference.toFixed(2)} EUR</Text>
                  )}
                </Banner>
              )}

              <InlineStack gap="800">
                <BlockStack gap="200">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Client
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {selectedOrder.customerEmail || 'Non renseigné'}
                  </Text>
                </BlockStack>
                <BlockStack gap="200">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Pays de livraison
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {getCountryName(selectedOrder.customerCountry)}
                  </Text>
                </BlockStack>
                <BlockStack gap="200">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Date
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {format(new Date(selectedOrder.orderDate), 'dd MMMM yyyy à HH:mm', { locale: fr })}
                  </Text>
                </BlockStack>
              </InlineStack>

              <InlineStack gap="800">
                <BlockStack gap="200">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Montant total
                  </Text>
                  <Text variant="headingMd" as="p">
                    {selectedOrder.totalAmount?.toFixed(2) ?? '0.00'} EUR
                  </Text>
                </BlockStack>
                <BlockStack gap="200">
                  <Text variant="bodySm" as="p" tone="subdued">
                    TVA appliquée
                  </Text>
                  <Text variant="headingMd" as="p">
                    {selectedOrder.taxAmount?.toFixed(2) ?? '0.00'} EUR ({selectedOrder.appliedVatRate ?? 0}%)
                  </Text>
                </BlockStack>
                <BlockStack gap="200">
                  <Text variant="bodySm" as="p" tone="subdued">
                    TVA attendue
                  </Text>
                  <Text variant="headingMd" as="p">
                    {selectedOrder.expectedVatRate}%
                  </Text>
                </BlockStack>
              </InlineStack>

              {selectedOrder.isB2b && (
                <Card>
                  <BlockStack gap="200">
                    <Text variant="headingSm" as="h3">
                      Informations B2B
                    </Text>
                    <InlineStack gap="400">
                      <BlockStack gap="100">
                        <Text variant="bodySm" as="p" tone="subdued">
                          N° TVA
                        </Text>
                        <Text variant="bodyMd" as="p">
                          {selectedOrder.customerVatNumber || 'Non renseigné'}
                        </Text>
                      </BlockStack>
                      <BlockStack gap="100">
                        <Text variant="bodySm" as="p" tone="subdued">
                          Validation VIES
                        </Text>
                        {selectedOrder.vatValidated ? (
                          <Badge tone="success">Validé</Badge>
                        ) : (
                          <Badge tone="warning">Non validé</Badge>
                        )}
                      </BlockStack>
                    </InlineStack>
                  </BlockStack>
                </Card>
              )}
            </BlockStack>
          )}
        </Modal.Section>
      </Modal>
    </Page>
  );
}

export default VatAnalysis;
