import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
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
  Checkbox,
  Select,
  Banner,
  Spinner,
  Box,
  Divider,
} from '@shopify/polaris';
import { settingsApi, billingApi } from '../services/api';
import type { Shop, Subscription } from '../types';

function Settings() {
  const queryClient = useQueryClient();
  const [ossRegistered, setOssRegistered] = useState(false);
  const [ossCountry, setOssCountry] = useState('FR');
  const [emailAlerts, setEmailAlerts] = useState(true);
  const [alertEmail, setAlertEmail] = useState('');

  const { data: shopInfo, isLoading: shopLoading } = useQuery<Shop>({
    queryKey: ['shop-info'],
    queryFn: async () => {
      const response = await settingsApi.getShopInfo();
      return response.data;
    },
    enabled: false, // Disable for demo
  });

  const { data: subscription, isLoading: subLoading } = useQuery<Subscription>({
    queryKey: ['subscription'],
    queryFn: async () => {
      const response = await billingApi.getSubscription();
      return response.data;
    },
    enabled: false, // Disable for demo
  });

  const saveMutation = useMutation({
    mutationFn: async (settings: Record<string, unknown>) => {
      const response = await settingsApi.updateSettings(settings);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shop-info'] });
    },
  });

  // Mock data for demo
  const mockShop: Shop = {
    id: 1,
    shopDomain: 'ma-boutique.myshopify.com',
    shopName: 'Ma Boutique',
    email: 'contact@maboutique.fr',
    country: 'FR',
    currency: 'EUR',
    ossRegistered: true,
    ossCountry: 'FR',
    subscriptionStatus: 'ACTIVE',
    subscriptionPlan: 'MONTHLY',
    createdAt: '2024-01-01T00:00:00Z',
  };

  const mockSubscription: Subscription = {
    status: 'ACTIVE',
    plan: 'MONTHLY',
    price: 99,
    billingCycle: 'monthly',
    nextBillingAt: '2024-02-15T00:00:00Z',
  };

  const shop = shopInfo || mockShop;
  const sub = subscription || mockSubscription;

  const euCountries = [
    { label: 'France', value: 'FR' },
    { label: 'Allemagne', value: 'DE' },
    { label: 'Belgique', value: 'BE' },
    { label: 'Espagne', value: 'ES' },
    { label: 'Italie', value: 'IT' },
    { label: 'Pays-Bas', value: 'NL' },
    { label: 'Autriche', value: 'AT' },
    { label: 'Portugal', value: 'PT' },
    { label: 'Pologne', value: 'PL' },
    { label: 'Suède', value: 'SE' },
    { label: 'Danemark', value: 'DK' },
    { label: 'Finlande', value: 'FI' },
    { label: 'Irlande', value: 'IE' },
    { label: 'Luxembourg', value: 'LU' },
    { label: 'République tchèque', value: 'CZ' },
    { label: 'Grèce', value: 'GR' },
    { label: 'Hongrie', value: 'HU' },
    { label: 'Roumanie', value: 'RO' },
    { label: 'Bulgarie', value: 'BG' },
    { label: 'Slovaquie', value: 'SK' },
    { label: 'Slovénie', value: 'SI' },
    { label: 'Croatie', value: 'HR' },
    { label: 'Lituanie', value: 'LT' },
    { label: 'Lettonie', value: 'LV' },
    { label: 'Estonie', value: 'EE' },
    { label: 'Chypre', value: 'CY' },
    { label: 'Malte', value: 'MT' },
  ];

  const handleSave = () => {
    saveMutation.mutate({
      ossRegistered,
      ossCountry,
      emailAlerts,
      alertEmail,
    });
  };

  const getSubscriptionBadge = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return <Badge tone="success">Actif</Badge>;
      case 'TRIAL':
        return <Badge tone="info">Essai gratuit</Badge>;
      case 'CANCELLED':
        return <Badge tone="warning">Annulé</Badge>;
      default:
        return <Badge tone="critical">Expiré</Badge>;
    }
  };

  if (shopLoading || subLoading) {
    return (
      <Page title="Paramètres">
        <Box padding="800">
          <InlineStack align="center">
            <Spinner size="large" />
          </InlineStack>
        </Box>
      </Page>
    );
  }

  return (
    <Page title="Paramètres" subtitle="Configuration de votre compte EU VAT Ease">
      <Layout>
        {/* Shop Info */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Informations de la boutique
              </Text>
              
              <InlineStack gap="800">
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Boutique
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {shop.shopName}
                  </Text>
                </BlockStack>
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Domaine
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {shop.shopDomain}
                  </Text>
                </BlockStack>
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Pays
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {shop.country}
                  </Text>
                </BlockStack>
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Devise
                  </Text>
                  <Text variant="bodyMd" as="p">
                    {shop.currency}
                  </Text>
                </BlockStack>
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Subscription */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <InlineStack align="space-between">
                <Text variant="headingMd" as="h2">
                  Abonnement
                </Text>
                {getSubscriptionBadge(sub.status)}
              </InlineStack>

              <InlineStack gap="800">
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Plan
                  </Text>
                  <Text variant="headingMd" as="p">
                    {sub.plan === 'MONTHLY' ? 'Mensuel' : 'Annuel'}
                  </Text>
                </BlockStack>
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Prix
                  </Text>
                  <Text variant="headingMd" as="p">
                    {sub.price}€ / {sub.billingCycle === 'monthly' ? 'mois' : 'an'}
                  </Text>
                </BlockStack>
                {sub.nextBillingAt && (
                  <BlockStack gap="100">
                    <Text variant="bodySm" as="p" tone="subdued">
                      Prochain prélèvement
                    </Text>
                    <Text variant="bodyMd" as="p">
                      {new Date(sub.nextBillingAt).toLocaleDateString('fr-FR')}
                    </Text>
                  </BlockStack>
                )}
              </InlineStack>

              <Divider />

              <InlineStack gap="300">
                <Button variant="primary">
                  {sub.plan === 'MONTHLY' ? 'Passer à l\'annuel (-17%)' : 'Gérer l\'abonnement'}
                </Button>
                <Button variant="plain" tone="critical">
                  Annuler l'abonnement
                </Button>
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* OSS Settings */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Configuration OSS
              </Text>

              <Banner>
                Le régime OSS (One-Stop Shop) simplifie la TVA pour les ventes B2C en UE. 
                Si vous vendez plus de 10 000€/an dans l'UE, vous devez vous inscrire.
              </Banner>

              <Checkbox
                label="Je suis inscrit au régime OSS"
                checked={ossRegistered}
                onChange={setOssRegistered}
              />

              {ossRegistered && (
                <Box maxWidth="300px">
                  <Select
                    label="Pays d'inscription OSS"
                    options={euCountries}
                    value={ossCountry}
                    onChange={setOssCountry}
                    helpText="Le pays où vous avez enregistré votre déclaration OSS"
                  />
                </Box>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Notifications */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Notifications
              </Text>

              <Checkbox
                label="Recevoir les alertes par email"
                checked={emailAlerts}
                onChange={setEmailAlerts}
                helpText="Erreurs TVA, rappels d'échéance OSS, seuil approchant..."
              />

              {emailAlerts && (
                <Box maxWidth="400px">
                  <TextField
                    label="Email de notification"
                    type="email"
                    value={alertEmail}
                    onChange={setAlertEmail}
                    placeholder={shop.email}
                    helpText="Laissez vide pour utiliser l'email de la boutique"
                    autoComplete="email"
                  />
                </Box>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Save Button */}
        <Layout.Section>
          <InlineStack align="end">
            <Button
              variant="primary"
              onClick={handleSave}
              loading={saveMutation.isPending}
            >
              Enregistrer les paramètres
            </Button>
          </InlineStack>
          
          {saveMutation.isSuccess && (
            <Banner tone="success">
              Paramètres enregistrés avec succès !
            </Banner>
          )}
        </Layout.Section>

        {/* Data & Privacy */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Données & Confidentialité
              </Text>

              <Text as="p" tone="subdued">
                Vos données sont stockées de manière sécurisée et en conformité avec le RGPD.
              </Text>

              <InlineStack gap="300">
                <Button variant="plain">
                  Exporter mes données
                </Button>
                <Button variant="plain" tone="critical">
                  Supprimer mon compte
                </Button>
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Support */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Support
              </Text>

              <InlineStack gap="800">
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      📧 Email
                    </Text>
                    <Text variant="bodySm" as="p">
                      support@euvatease.com
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      📚 Documentation
                    </Text>
                    <Text variant="bodySm" as="p">
                      docs.euvatease.com
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="200px">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      💬 Chat
                    </Text>
                    <Text variant="bodySm" as="p">
                      Disponible en heure ouvrée
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

export default Settings;
