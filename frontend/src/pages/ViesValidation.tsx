import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Page,
  Layout,
  Card,
  Text,
  BlockStack,
  InlineStack,
  TextField,
  Button,
  Badge,
  DataTable,
  Banner,
  Spinner,
  Box,
} from '@shopify/polaris';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { viesApi } from '../services/api';
import type { VatValidation } from '../types';

function ViesValidation() {
  const [vatNumber, setVatNumber] = useState('');
  const queryClient = useQueryClient();

  const { data: history, isLoading: historyLoading } = useQuery<VatValidation[]>({
    queryKey: ['vies-history'],
    queryFn: async () => {
      const response = await viesApi.getValidationHistory({ size: 20 });
      return response.data.content;
    },
  });

  const validateMutation = useMutation({
    mutationFn: async (vatNumber: string) => {
      const response = await viesApi.validateVatNumber(vatNumber);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vies-history'] });
      setVatNumber('');
    },
  });

  // Mock data for demo
  const mockHistory: VatValidation[] = [
    {
      id: 1,
      vatNumber: 'DE123456789',
      countryCode: 'DE',
      valid: true,
      companyName: 'Example GmbH',
      companyAddress: 'Musterstraße 123, 10115 Berlin',
      validatedAt: '2024-01-15T10:30:00Z',
      viesRequestId: 'WAPIAAAAWCN94U8L',
    },
    {
      id: 2,
      vatNumber: 'BE0123456789',
      countryCode: 'BE',
      valid: true,
      companyName: 'Exemple SPRL',
      companyAddress: 'Rue de Test 45, 1000 Bruxelles',
      validatedAt: '2024-01-14T14:20:00Z',
      viesRequestId: 'WAPIAAAAWCN94U8M',
    },
    {
      id: 3,
      vatNumber: 'FR12345678901',
      countryCode: 'FR',
      valid: false,
      validatedAt: '2024-01-13T09:15:00Z',
      errorMessage: 'Numéro de TVA invalide',
    },
    {
      id: 4,
      vatNumber: 'NL123456789B01',
      countryCode: 'NL',
      valid: true,
      companyName: 'Voorbeeld BV',
      companyAddress: 'Teststraat 78, 1011 AB Amsterdam',
      validatedAt: '2024-01-12T16:45:00Z',
      viesRequestId: 'WAPIAAAAWCN94U8N',
    },
  ];

  const validations = history || mockHistory;

  const getCountryFlag = (code: string): string => {
    const codePoints = code
      .toUpperCase()
      .split('')
      .map((char) => 127397 + char.charCodeAt(0));
    return String.fromCodePoint(...codePoints);
  };

  const handleValidate = () => {
    if (vatNumber.trim()) {
      validateMutation.mutate(vatNumber.trim().toUpperCase().replace(/\s/g, ''));
    }
  };

  const tableRows = validations.map((v: VatValidation) => [
    <InlineStack gap="200" blockAlign="center">
      <span>{getCountryFlag(v.countryCode)}</span>
      <Text variant="bodyMd" as="span" fontWeight="semibold">
        {v.vatNumber}
      </Text>
    </InlineStack>,
    v.companyName || '-',
    format(new Date(v.validatedAt), 'dd MMM yyyy HH:mm', { locale: fr }),
    v.valid ? (
      <Badge tone="success">Valide</Badge>
    ) : (
      <Badge tone="critical">Invalide</Badge>
    ),
    v.viesRequestId || '-',
  ]);

  return (
    <Page
      title="Validation VIES"
      subtitle="Vérifiez les numéros de TVA intracommunautaires"
    >
      <Layout>
        {/* Validation Form */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Vérifier un numéro de TVA
              </Text>
              
              <Banner>
                La validation VIES est une vérification officielle auprès de la Commission Européenne.
                Elle génère une preuve légale horodatée avec un identifiant unique.
              </Banner>

              <InlineStack gap="400" blockAlign="end">
                <Box minWidth="400px">
                  <TextField
                    label="Numéro de TVA intracommunautaire"
                    value={vatNumber}
                    onChange={setVatNumber}
                    placeholder="Ex: FR12345678901, DE123456789, BE0123456789"
                    autoComplete="off"
                    helpText="Entrez le numéro avec le préfixe pays (2 lettres)"
                  />
                </Box>
                <Button 
                  variant="primary" 
                  onClick={handleValidate}
                  loading={validateMutation.isPending}
                  disabled={!vatNumber.trim()}
                >
                  Valider
                </Button>
              </InlineStack>

              {/* Validation Result */}
              {validateMutation.isSuccess && validateMutation.data && (
                <Card>
                  <BlockStack gap="300">
                    <InlineStack align="space-between">
                      <Text variant="headingSm" as="h3">
                        Résultat de la validation
                      </Text>
                      {validateMutation.data.valid ? (
                        <Badge tone="success" size="large">
                          ✓ VALIDE
                        </Badge>
                      ) : (
                        <Badge tone="critical" size="large">
                          ✗ INVALIDE
                        </Badge>
                      )}
                    </InlineStack>

                    {validateMutation.data.valid ? (
                      <>
                        <InlineStack gap="800">
                          <BlockStack gap="100">
                            <Text variant="bodySm" as="p" tone="subdued">
                              Entreprise
                            </Text>
                            <Text variant="bodyMd" as="p">
                              {validateMutation.data.companyName}
                            </Text>
                          </BlockStack>
                          <BlockStack gap="100">
                            <Text variant="bodySm" as="p" tone="subdued">
                              Adresse
                            </Text>
                            <Text variant="bodyMd" as="p">
                              {validateMutation.data.companyAddress}
                            </Text>
                          </BlockStack>
                        </InlineStack>
                        <Banner tone="success">
                          <Text as="p">
                            <strong>Preuve légale générée</strong> - ID: {validateMutation.data.viesRequestId}
                          </Text>
                          <Text as="p" tone="subdued">
                            Conservez cet identifiant comme preuve de vérification pour vos archives fiscales.
                          </Text>
                        </Banner>
                      </>
                    ) : (
                      <Banner tone="critical">
                        {validateMutation.data.errorMessage || 
                          'Ce numéro de TVA n\'est pas enregistré dans le système VIES.'}
                      </Banner>
                    )}
                  </BlockStack>
                </Card>
              )}

              {validateMutation.isError && (
                <Banner tone="warning">
                  Le service VIES est temporairement indisponible. 
                  Veuillez réessayer dans quelques minutes.
                </Banner>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Validation History */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <InlineStack align="space-between">
                <Text variant="headingMd" as="h2">
                  Historique des validations
                </Text>
                <Button variant="plain">
                  Exporter l'historique
                </Button>
              </InlineStack>

              {historyLoading ? (
                <Box padding="400">
                  <InlineStack align="center">
                    <Spinner size="small" />
                  </InlineStack>
                </Box>
              ) : (
                <DataTable
                  columnContentTypes={['text', 'text', 'text', 'text', 'text']}
                  headings={['N° TVA', 'Entreprise', 'Date', 'Statut', 'ID VIES']}
                  rows={tableRows}
                />
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Info Section */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                À propos de VIES
              </Text>
              <Text as="p">
                VIES (VAT Information Exchange System) est le système officiel de la Commission Européenne 
                pour vérifier la validité des numéros de TVA intracommunautaires.
              </Text>
              <InlineStack gap="400">
                <Box padding="300" background="bg-surface-secondary" borderRadius="200">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      🛡️ Preuve légale
                    </Text>
                    <Text variant="bodySm" as="p">
                      Chaque validation génère un identifiant unique qui sert de preuve fiscale.
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      ⏱️ En temps réel
                    </Text>
                    <Text variant="bodySm" as="p">
                      Les données sont vérifiées directement auprès des administrations fiscales.
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      🔄 27 pays
                    </Text>
                    <Text variant="bodySm" as="p">
                      Validation pour tous les États membres de l'Union Européenne.
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

export default ViesValidation;
