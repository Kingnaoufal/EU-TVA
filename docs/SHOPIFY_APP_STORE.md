# Guide de soumission sur l'App Store Shopify

Ce guide détaille les étapes pour soumettre EU VAT Ease sur le Shopify App Store.

## Prérequis

1. Compte Shopify Partner vérifié
2. Application créée dans le Partner Dashboard
3. Application fonctionnelle et testée
4. Toutes les URLs configurées avec HTTPS

## 1. Configuration de l'application

### 1.1 App Setup (Partner Dashboard)

Aller dans **Apps** → **Your App** → **App setup**

#### URLs
- **App URL**: `https://app.euvatease.com`
- **Allowed redirection URL(s)**: 
  ```
  https://app.euvatease.com/auth/shopify/callback
  ```
- **App proxy URL** (optionnel): Non requis

#### GDPR Webhooks
- **Customer data request**: `https://api.euvatease.com/webhooks/gdpr/customers/data_request`
- **Customer data erasure**: `https://api.euvatease.com/webhooks/gdpr/customers/redact`
- **Shop data erasure**: `https://api.euvatease.com/webhooks/gdpr/shop/redact`

### 1.2 API Scopes

Scopes requis (dans **API access**):
```
read_orders
write_orders
read_customers
```

## 2. App Listing

### 2.1 Informations de base

**App name**: EU VAT Ease

**Tagline** (80 caractères max):
```
Automatisez votre conformité TVA UE et vos rapports OSS
```

**Description** (courte, 100 mots):
```
EU VAT Ease détecte automatiquement les erreurs de TVA sur vos commandes européennes et génère vos rapports OSS trimestriels. Validez les numéros de TVA intracommunautaires via VIES, recevez des alertes en cas d'anomalie, et exportez vos rapports en CSV ou PDF. Conçu pour les micro-businesses qui vendent en UE. Essai gratuit 14 jours.
```

**Description** (longue, 400 mots):
```
EU VAT Ease est la solution complète pour gérer la TVA européenne sur votre boutique Shopify.

🔍 DÉTECTION D'ERREURS TVA
Notre système analyse automatiquement chaque commande et identifie les erreurs de TVA :
- Taux incorrect appliqué pour un pays
- TVA facturée sur une vente B2B intracommunautaire
- TVA manquante sur une vente B2C

✅ VALIDATION VIES OFFICIELLE
Vérifiez les numéros de TVA intracommunautaires de vos clients B2B directement via le système VIES de la Commission Européenne. Chaque validation génère une preuve légale horodatée avec un identifiant unique.

📊 RAPPORTS OSS AUTOMATIQUES
Générez vos rapports trimestriels conformes au format OSS en un clic :
- Ventilation par pays et par taux
- Export CSV pour votre logiciel comptable
- Export PDF pour vos archives
- Rappels automatiques avant les échéances

🔔 ALERTES INTELLIGENTES
Soyez prévenu immédiatement :
- Erreur de TVA détectée sur une commande
- Approche du seuil OSS de 10 000€
- Échéance de déclaration OSS
- Changement de taux TVA dans un pays UE

💼 CONÇU POUR LES MICRO-BUSINESSES
Tarification adaptée aux petits e-commerçants qui commencent à vendre en Europe. Pas de commission sur les ventes, prix fixe mensuel.

🛡️ SÉCURITÉ & CONFORMITÉ
- Données chiffrées
- Conformité RGPD
- Serveurs en Europe
- Audit trail complet

Démarrez votre essai gratuit de 14 jours et découvrez comment EU VAT Ease peut vous faire gagner du temps et éviter les erreurs coûteuses.
```

### 2.2 Catégories

- **Primary category**: Finance
- **Secondary category**: Taxes

### 2.3 Pricing

- **Pricing plan**: Recurring charge
- **Monthly price**: $99 USD (ou équivalent EUR)
- **Annual price**: $999 USD (17% discount)
- **Trial days**: 14

### 2.4 Visuels

#### App icon (1200x1200 px)
- Fond: Bleu #0066FF
- Icône: Drapeau EU stylisé avec symbole check

#### Screenshots (1600x900 px minimum, 4-8 images)
1. Dashboard principal avec stats
2. Page d'analyse TVA avec liste des commandes
3. Validation VIES avec résultat
4. Rapport OSS généré
5. Page des alertes
6. Page des paramètres

#### Feature video (optionnel, 720p minimum)
- Durée: 30-60 secondes
- Démonstration du workflow principal

### 2.5 Support

- **Email**: support@euvatease.com
- **Documentation URL**: https://docs.euvatease.com
- **FAQ URL**: https://euvatease.com/faq
- **Privacy policy URL**: https://euvatease.com/privacy
- **Terms of service URL**: https://euvatease.com/terms

### 2.6 Keywords (SEO)

```
TVA, VAT, OSS, Europe, EU, taxe, impôt, compliance, conformité, rapport, VIES, intracommunautaire, B2B, déclaration
```

## 3. Review Checklist

Avant de soumettre, vérifier :

### Fonctionnel
- [ ] Installation OAuth fonctionne
- [ ] Webhooks reçus et traités
- [ ] Billing API fonctionne
- [ ] Désinstallation propre (suppression données)
- [ ] GDPR webhooks implémentés

### UI/UX
- [ ] Utilise Shopify Polaris
- [ ] App Bridge 4.x intégré
- [ ] Responsive design
- [ ] Loading states
- [ ] Error handling

### Sécurité
- [ ] HTTPS partout
- [ ] Validation HMAC webhooks
- [ ] JWT sécurisé
- [ ] Rate limiting

### Documentation
- [ ] Privacy policy à jour
- [ ] Terms of service à jour
- [ ] FAQ complète

## 4. Soumission

1. Aller dans **App setup** → **Distribution**
2. Choisir **Shopify App Store**
3. Compléter le formulaire de listing
4. Cliquer **Submit for review**

## 5. Post-soumission

Le review prend généralement 3-7 jours ouvrés.

### Motifs de rejet courants
- Scopes non justifiés
- UI non conforme Polaris
- Bugs dans le flow d'installation
- Webhooks GDPR non fonctionnels
- Descriptions marketing exagérées

### Après approbation
1. Surveiller les premières installations
2. Répondre rapidement aux reviews
3. Itérer sur les retours utilisateurs

## 6. Maintenance

### Updates
- Tester sur boutique de dev avant de déployer
- Informer les utilisateurs des changements majeurs
- Maintenir la compatibilité avec les API Shopify

### Monitoring
- Surveiller les erreurs via logs
- Tracker les métriques clés (churn, conversion)
- Répondre aux tickets support < 24h
