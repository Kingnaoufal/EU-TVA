# EU VAT Ease 🇪🇺

**La TVA UE sur Shopify, sans erreurs ni stress.**

Plugin Shopify pour les micro-business européens qui veulent automatiser leur conformité TVA UE et le régime OSS.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)

## 🎯 Fonctionnalités

### TVA & Conformité
- ✅ **Détection automatique des erreurs TVA** - Analyse chaque commande et identifie les anomalies
- ✅ **Validation VIES officielle** - Vérification des numéros de TVA intracommunautaires avec preuve légale
- ✅ **Taux TVA à jour** - Base de données des 27 pays de l'UE

### Rapports OSS
- 📊 **Génération automatique** - Rapports trimestriels conformes
- 📥 **Export CSV & PDF** - Formats prêts à soumettre
- ⏰ **Alertes d'échéance** - Rappels avant les dates limites

### Shopify Integration
- 🔗 **OAuth 2.0** - Connexion sécurisée à votre boutique
- 🔄 **Webhooks temps réel** - Synchronisation automatique des commandes
- 💳 **Billing API** - Abonnement via Shopify

## 🏗️ Architecture

```
eu-tva/
├── backend/           # API Spring Boot (Java 17)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/euvatease/
│   │   │   │   ├── config/        # Security, CORS, etc.
│   │   │   │   ├── controller/    # REST endpoints
│   │   │   │   ├── entity/        # JPA entities
│   │   │   │   ├── repository/    # Data access
│   │   │   │   └── service/       # Business logic
│   │   │   └── resources/
│   │   │       ├── db/migration/  # Flyway migrations
│   │   │       └── templates/     # Email templates
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/          # React + Shopify Polaris
│   ├── src/
│   │   ├── pages/     # Dashboard, VatAnalysis, etc.
│   │   ├── services/  # API client
│   │   └── types/     # TypeScript types
│   ├── Dockerfile
│   └── package.json
│
├── landing/           # Landing page (HTML/CSS)
│   ├── index.html
│   └── netlify.toml
│
├── docker-compose.yml
├── .env.example
└── README.md
```

## 🚀 Démarrage rapide

### Prérequis

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Docker & Docker Compose (optionnel)

### 1. Configuration

```bash
# Cloner le repo
git clone https://github.com/your-org/eu-tva.git
cd eu-tva

# Copier et configurer les variables d'environnement
cp .env.example .env
# Éditer .env avec vos valeurs
```

### 2. Lancement avec Docker (recommandé)

```bash
docker-compose up -d
```

L'application sera disponible sur:
- Frontend: http://localhost
- Backend API: http://localhost:8080
- Landing page: http://localhost:8081

### 3. Lancement manuel (développement)

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

## 📡 API Endpoints

### Authentication
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/auth/shopify` | Initier OAuth Shopify |
| GET | `/auth/shopify/callback` | Callback OAuth |

### VAT Analysis
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/vat/dashboard` | Stats du tableau de bord |
| GET | `/api/vat/orders` | Liste des commandes UE |
| POST | `/api/vat/vies/validate` | Valider un numéro TVA |

### OSS Reports
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/oss/reports` | Liste des rapports |
| POST | `/api/oss/reports/generate` | Générer un rapport |
| GET | `/api/oss/reports/{id}/csv` | Télécharger CSV |
| GET | `/api/oss/reports/{id}/pdf` | Télécharger PDF |

### Webhooks Shopify
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/webhooks/shopify/orders/create` | Nouvelle commande |
| POST | `/webhooks/shopify/orders/updated` | Commande modifiée |
| POST | `/webhooks/shopify/refunds/create` | Remboursement |
| POST | `/webhooks/shopify/app/uninstalled` | Désinstallation |

## 💶 Tarification

| Plan | Prix | Facturation |
|------|------|-------------|
| Mensuel | 99€ | Par mois |
| Annuel | 999€ | Par an (2 mois offerts) |

**Inclus dans tous les plans:**
- Commandes illimitées
- Rapports OSS illimités
- Validation VIES illimitée
- Support email
- 14 jours d'essai gratuit

## 🔒 Sécurité

- Authentification JWT
- Validation HMAC des webhooks Shopify
- Chiffrement des données sensibles
- Conformité RGPD
- Audit log complet

## 🌍 Taux TVA UE (2024)

| Pays | Taux standard |
|------|---------------|
| 🇫🇷 France | 20% |
| 🇩🇪 Allemagne | 19% |
| 🇪🇸 Espagne | 21% |
| 🇮🇹 Italie | 22% |
| 🇧🇪 Belgique | 21% |
| 🇳🇱 Pays-Bas | 21% |
| ... | ... |

*Tous les 27 pays de l'UE sont supportés.*

## 📝 Hypothèses de conception

1. **Extraction du numéro TVA B2B** : Recherche dans `order.note`, `note_attributes` (clés: `vat_number`, `tax_id`, `company_vat`), et métafields client.

2. **Détection B2B/B2C** : Une commande est B2B si un numéro de TVA valide est fourni et validé via VIES.

3. **Calcul de la TVA** : Extraction depuis `tax_lines` de Shopify. Si absent, calcul inversé depuis le total TTC.

4. **Seuil OSS** : Basé sur le pays de livraison (`shipping_address.country`), pas le pays de facturation.

5. **Retry VIES** : En cas d'indisponibilité du service VIES, retry automatique toutes les 15 minutes (max 3 tentatives).

6. **Devises** : Les montants en devises autres que EUR sont convertis au taux Shopify du jour de la commande.

## 🛠️ Configuration Shopify Partner

### 1. Créer l'application
1. Aller sur [partners.shopify.com](https://partners.shopify.com)
2. Apps → Create app
3. Choisir "Custom app"

### 2. Configurer les URLs
- App URL: `https://app.euvatease.com`
- Allowed redirection URLs: `https://app.euvatease.com/auth/shopify/callback`

### 3. Configurer les scopes
```
read_orders
write_orders
read_customers
```

### 4. Configurer les webhooks
Dans l'admin Shopify de chaque boutique:
- `orders/create` → `https://api.euvatease.com/webhooks/shopify/orders/create`
- `orders/updated` → `https://api.euvatease.com/webhooks/shopify/orders/updated`
- `refunds/create` → `https://api.euvatease.com/webhooks/shopify/refunds/create`
- `app/uninstalled` → `https://api.euvatease.com/webhooks/shopify/app/uninstalled`

## 📦 Déploiement Production

### Option 1: Docker sur VPS

```bash
# Sur votre serveur
git clone https://github.com/your-org/eu-tva.git
cd eu-tva
cp .env.example .env
# Éditer .env
docker-compose -f docker-compose.yml up -d
```

### Option 2: Kubernetes

Helm charts disponibles dans `/k8s` (à venir).

### Option 3: Services managés

- **Backend**: Railway, Render, Heroku
- **Database**: Supabase, Neon, Railway
- **Frontend**: Vercel, Netlify
- **Landing**: Netlify (déjà configuré via `netlify.toml`)

## 🧪 Tests

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
npm test
```

## 📄 License

MIT License - voir [LICENSE](LICENSE)

## 🤝 Support

- 📧 Email: support@euvatease.com
- 📚 Documentation: docs.euvatease.com
- 💬 Chat: Disponible dans l'application

---

**Fait avec ❤️ pour les entrepreneurs européens**
