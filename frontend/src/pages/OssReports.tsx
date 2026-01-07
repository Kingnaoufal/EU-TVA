import { useState } from 'react';
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
  DataTable,
  Select,
  Modal,
  Banner,
  Spinner,
  Box,
  ProgressBar,
} from '@shopify/polaris';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { ossApi } from '../services/api';
import type { OssReport, OssReportLine } from '../types';

function OssReports() {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear().toString());
  const [selectedQuarter, setSelectedQuarter] = useState(
    Math.ceil((new Date().getMonth() + 1) / 3).toString()
  );
  const [generatingReport, setGeneratingReport] = useState(false);
  const [selectedReport, setSelectedReport] = useState<OssReport | null>(null);
  const [modalActive, setModalActive] = useState(false);
  const queryClient = useQueryClient();

  const { data: reportsData, isLoading } = useQuery<{ reports: OssReport[]; nextDeadline?: string }>({
    queryKey: ['oss-reports'],
    queryFn: async () => {
      const response = await ossApi.getReports();
      return response.data;
    },
  });

  const generateMutation = useMutation({
    mutationFn: async ({ year, quarter }: { year: number; quarter: number }) => {
      setGeneratingReport(true);
      const response = await ossApi.generateReport(year, quarter);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['oss-reports'] });
      setGeneratingReport(false);
    },
    onError: () => {
      setGeneratingReport(false);
    },
  });

  const ossReports = reportsData?.reports || [];

  const yearOptions = [
    { label: '2024', value: '2024' },
    { label: '2023', value: '2023' },
    { label: '2022', value: '2022' },
  ];

  const quarterOptions = [
    { label: 'T1 (Jan-Mar)', value: '1' },
    { label: 'T2 (Avr-Jun)', value: '2' },
    { label: 'T3 (Jul-Sep)', value: '3' },
    { label: 'T4 (Oct-Déc)', value: '4' },
  ];

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'SUBMITTED':
        return <Badge tone="success">Soumis</Badge>;
      case 'GENERATED':
        return <Badge tone="info">Généré</Badge>;
      default:
        return <Badge>Brouillon</Badge>;
    }
  };

  const handleGenerate = () => {
    generateMutation.mutate({
      year: parseInt(selectedYear),
      quarter: parseInt(selectedQuarter),
    });
  };

  const handleViewReport = (report: OssReport) => {
    setSelectedReport(report);
    setModalActive(true);
  };

  const handleDownloadCsv = async (reportId: number) => {
    try {
      const response = await ossApi.downloadCsv(reportId);
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `rapport-oss-${reportId}.csv`;
      a.click();
    } catch (error) {
      console.error('Error downloading CSV:', error);
    }
  };

  const handleDownloadPdf = async (reportId: number) => {
    try {
      const response = await ossApi.downloadPdf(reportId);
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `rapport-oss-${reportId}.pdf`;
      a.click();
    } catch (error) {
      console.error('Error downloading PDF:', error);
    }
  };

  const tableRows = ossReports.map((report: OssReport) => [
    `T${report.quarter} ${report.year}`,
    `${report.totalSales.toFixed(2)} €`,
    `${report.totalVat.toFixed(2)} €`,
    format(new Date(report.generatedAt), 'dd MMM yyyy', { locale: fr }),
    getStatusBadge(report.status),
    <InlineStack gap="200">
      <Button variant="plain" onClick={() => handleViewReport(report)}>
        Voir
      </Button>
      <Button variant="plain" onClick={() => handleDownloadCsv(report.id)}>
        CSV
      </Button>
      <Button variant="plain" onClick={() => handleDownloadPdf(report.id)}>
        PDF
      </Button>
    </InlineStack>,
  ]);

  // Calculate next deadline
  const now = new Date();
  const currentQuarter = Math.ceil((now.getMonth() + 1) / 3);
  const nextDeadlineMonth = currentQuarter * 3 + 1; // Month after quarter ends
  const nextDeadlineYear = nextDeadlineMonth > 12 ? now.getFullYear() + 1 : now.getFullYear();
  const adjustedMonth = nextDeadlineMonth > 12 ? 1 : nextDeadlineMonth;
  const nextDeadline = new Date(nextDeadlineYear, adjustedMonth - 1, 20);
  const daysUntilDeadline = Math.ceil((nextDeadline.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));

  if (isLoading) {
    return (
      <Page title="Rapports OSS">
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
      title="Rapports OSS"
      subtitle="Générez et téléchargez vos rapports trimestriels OSS"
    >
      <Layout>
        {/* Deadline Banner */}
        <Layout.Section>
          <Card>
            <BlockStack gap="300">
              <InlineStack align="space-between">
                <BlockStack gap="100">
                  <Text variant="headingMd" as="h2">
                    Prochaine échéance OSS
                  </Text>
                  <Text as="p" tone="subdued">
                    T{currentQuarter} {now.getFullYear()} - Date limite: {format(nextDeadline, 'dd MMMM yyyy', { locale: fr })}
                  </Text>
                </BlockStack>
                <Badge tone={daysUntilDeadline <= 7 ? 'critical' : daysUntilDeadline <= 14 ? 'warning' : 'info'}>
                  {`${daysUntilDeadline} jours restants`}
                </Badge>
              </InlineStack>
              <ProgressBar 
                progress={Math.max(0, 100 - (daysUntilDeadline / 90) * 100)} 
                tone={daysUntilDeadline <= 7 ? 'critical' : 'highlight'}
              />
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Generate Report */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Générer un nouveau rapport
              </Text>

              <InlineStack gap="400" blockAlign="end">
                <Box minWidth="150px">
                  <Select
                    label="Année"
                    options={yearOptions}
                    value={selectedYear}
                    onChange={setSelectedYear}
                  />
                </Box>
                <Box minWidth="150px">
                  <Select
                    label="Trimestre"
                    options={quarterOptions}
                    value={selectedQuarter}
                    onChange={setSelectedQuarter}
                  />
                </Box>
                <Button
                  variant="primary"
                  onClick={handleGenerate}
                  loading={generatingReport}
                >
                  Générer le rapport
                </Button>
              </InlineStack>

              {generateMutation.isSuccess && (
                <Banner tone="success">
                  Le rapport T{selectedQuarter} {selectedYear} a été généré avec succès !
                </Banner>
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* Reports List */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                Historique des rapports
              </Text>

              {ossReports.length === 0 ? (
                <Text as="p" tone="subdued">
                  Aucun rapport généré pour le moment.
                </Text>
              ) : (
                <DataTable
                  columnContentTypes={['text', 'numeric', 'numeric', 'text', 'text', 'text']}
                  headings={['Période', 'Ventes', 'TVA', 'Généré le', 'Statut', 'Actions']}
                  rows={tableRows}
                />
              )}
            </BlockStack>
          </Card>
        </Layout.Section>

        {/* OSS Info */}
        <Layout.Section>
          <Card>
            <BlockStack gap="400">
              <Text variant="headingMd" as="h2">
                À propos du régime OSS
              </Text>
              <InlineStack gap="400">
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="250px">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      📅 Échéances trimestrielles
                    </Text>
                    <Text variant="bodySm" as="p">
                      Déclaration avant le 20 du mois suivant chaque trimestre.
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="250px">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      🌍 Guichet unique
                    </Text>
                    <Text variant="bodySm" as="p">
                      Une seule déclaration pour tous les pays de l'UE.
                    </Text>
                  </BlockStack>
                </Box>
                <Box padding="300" background="bg-surface-secondary" borderRadius="200" minWidth="250px">
                  <BlockStack gap="100">
                    <Text variant="headingSm" as="h3">
                      💶 Seuil 10 000 €
                    </Text>
                    <Text variant="bodySm" as="p">
                      Obligatoire au-delà de 10 000 € de ventes UE/an.
                    </Text>
                  </BlockStack>
                </Box>
              </InlineStack>
            </BlockStack>
          </Card>
        </Layout.Section>
      </Layout>

      {/* Report Detail Modal */}
      <Modal
        open={modalActive}
        onClose={() => setModalActive(false)}
        title={`Rapport OSS T${selectedReport?.quarter} ${selectedReport?.year}`}
        primaryAction={{
          content: 'Télécharger PDF',
          onAction: () => selectedReport && handleDownloadPdf(selectedReport.id),
        }}
        secondaryActions={[
          {
            content: 'Télécharger CSV',
            onAction: () => selectedReport && handleDownloadCsv(selectedReport.id),
          },
        ]}
        size="large"
      >
        <Modal.Section>
          {selectedReport && (
            <BlockStack gap="400">
              <InlineStack gap="800">
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Total des ventes
                  </Text>
                  <Text variant="headingLg" as="p">
                    {selectedReport.totalSales.toFixed(2)} €
                  </Text>
                </BlockStack>
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    TVA totale
                  </Text>
                  <Text variant="headingLg" as="p">
                    {selectedReport.totalVat.toFixed(2)} €
                  </Text>
                </BlockStack>
                <BlockStack gap="100">
                  <Text variant="bodySm" as="p" tone="subdued">
                    Statut
                  </Text>
                  {getStatusBadge(selectedReport.status)}
                </BlockStack>
              </InlineStack>

              {selectedReport.lines.length > 0 && (
                <>
                  <Text variant="headingSm" as="h3">
                    Détail par pays
                  </Text>
                  <DataTable
                    columnContentTypes={['text', 'numeric', 'numeric', 'numeric', 'numeric']}
                    headings={['Pays', 'Taux TVA', 'Base imposable', 'TVA', 'Commandes']}
                    rows={selectedReport.lines.map((line: OssReportLine) => [
                      line.countryName,
                      `${line.vatRate}%`,
                      `${line.taxableAmount.toFixed(2)} €`,
                      `${line.vatAmount.toFixed(2)} €`,
                      line.orderCount.toString(),
                    ])}
                    totals={[
                      'Total',
                      '',
                      `${selectedReport.totalSales.toFixed(2)} €`,
                      `${selectedReport.totalVat.toFixed(2)} €`,
                      selectedReport.lines.reduce((acc: number, l: OssReportLine) => acc + l.orderCount, 0).toString(),
                    ]}
                  />
                </>
              )}
            </BlockStack>
          )}
        </Modal.Section>
      </Modal>
    </Page>
  );
}

export default OssReports;
