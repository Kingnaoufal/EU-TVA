# 🚀 Guide de Déploiement - EU VAT Ease

Ce guide vous accompagne dans le déploiement complet de l'application EU VAT Ease en production.

## 📋 Table des matières

1. [Prérequis](#prérequis)
2. [Architecture de déploiement](#architecture-de-déploiement)
3. [Variables d'environnement](#variables-denvironnement)
4. [Déploiement de la base de données](#déploiement-de-la-base-de-données)
5. [Déploiement du Backend](#déploiement-du-backend)
6. [Déploiement du Frontend](#déploiement-du-frontend)
7. [Configuration Shopify](#configuration-shopify)
8. [Configuration DNS et SSL](#configuration-dns-et-ssl)
9. [Monitoring et Logs](#monitoring-et-logs)
10. [Checklist de production](#checklist-de-production)

---

## 🔧 Prérequis

### Comptes requis
- [ ] Compte [Shopify Partners](https://partners.shopify.com/)
- [ ] Compte cloud (Railway, Render, AWS, GCP, ou Azure)
- [ ] Compte pour l'hébergement frontend (Vercel, Netlify, ou Cloudflare Pages)
- [ ] Service SMTP (SendGrid, Mailgun, ou AWS SES)
- [ ] Domaine personnalisé (optionnel mais recommandé)

### Outils locaux
- Java 21+
- Node.js 18+
- Docker (optionnel)
- Git

---

## 🏗️ Architecture de déploiement

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    Shopify      │────▶│    Frontend     │────▶│    Backend      │
│    Admin        │     │  (Vercel/etc)   │     │ (Railway/etc)   │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
                        ┌─────────────────┐              │
                        │   PostgreSQL    │◀─────────────┘
                        │   (Database)    │
                        └─────────────────┘
```

### Flux de données
1. **Shopify** → Envoie les webhooks et demandes OAuth
2. **Frontend** → Interface React embarquée dans Shopify Admin
3. **Backend** → API Spring Boot, traitement TVA, validation VIES
4. **PostgreSQL** → Stockage des données

---

## 🔐 Variables d'environnement

### Variables Backend (OBLIGATOIRES en production)

```bash
# ===== BASE DE DONNÉES =====
DATABASE_URL=jdbc:postgresql://host:5432/euvatease
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_secure_db_password

# ===== SHOPIFY =====
SHOPIFY_API_KEY=your_shopify_api_key
SHOPIFY_API_SECRET=your_shopify_api_secret
SHOPIFY_WEBHOOK_SECRET=your_webhook_secret
SHOPIFY_REDIRECT_URI=https://api.yourdomain.com/api/shopify/callback
SHOPIFY_APP_URL=https://api.yourdomain.com

# ===== SÉCURITÉ =====
JWT_SECRET=your-very-long-and-secure-jwt-secret-minimum-256-bits
ENCRYPTION_KEY=your-32-character-encryption-key!

# ===== EMAIL =====
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=your_sendgrid_api_key

# ===== APPLICATION =====
FRONTEND_URL=https://app.yourdomain.com
PORT=8080
```

### Variables Frontend

```bash
VITE_API_URL=https://api.yourdomain.com/api
VITE_SHOPIFY_API_KEY=your_shopify_api_key
```

### Générer des clés sécurisées

```bash
# JWT Secret (256 bits minimum)
openssl rand -base64 32

# Encryption Key (32 caractères)
openssl rand -base64 24

# Webhook Secret
openssl rand -hex 32
```

---

## 🐘 Déploiement de la base de données

### Option 1: Railway (Recommandé pour commencer)

1. Créez un compte sur [Railway](https://railway.app/)
2. Nouveau projet → Add PostgreSQL
3. Récupérez les credentials dans l'onglet "Connect"

```bash
# Format de DATABASE_URL fourni par Railway
postgresql://user:password@host:port/database
```

### Option 2: Supabase

1. Créez un projet sur [Supabase](https://supabase.com/)
2. Settings → Database → Connection string
3. Utilisez le "URI" fourni

### Option 3: AWS RDS

```bash
# Créer une instance PostgreSQL
aws rds create-db-instance \
  --db-instance-identifier euvatease-prod \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username admin \
  --master-user-password your_password \
  --allocated-storage 20
```

### Option 4: Docker (Auto-hébergé)

```yaml
# docker-compose.db.yml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: euvatease
      POSTGRES_USER: euvatease
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: unless-stopped

volumes:
  postgres_data:
```

---

## ☕ Déploiement du Backend

### Option 1: Railway (Recommandé)

#### 1. Préparer le projet

Créez un fichier `Procfile` à la racine du backend :

```
web: java -jar -Dserver.port=$PORT target/*.jar
```

Créez `system.properties` :

```
java.runtime.version=21
```

#### 2. Déployer

```bash
# Dans le dossier backend
railway login
railway init
railway link

# Ajouter les variables d'environnement
railway variables set DATABASE_URL="..."
railway variables set SHOPIFY_API_KEY="..."
# ... autres variables

# Déployer
railway up
```

### Option 2: Render

#### 1. Créez un `render.yaml`

```yaml
services:
  - type: web
    name: euvatease-api
    env: docker
    dockerfilePath: ./backend/Dockerfile
    envVars:
      - key: DATABASE_URL
        fromDatabase:
          name: euvatease-db
          property: connectionString
      - key: SHOPIFY_API_KEY
        sync: false
      - key: SHOPIFY_API_SECRET
        sync: false
      - key: JWT_SECRET
        generateValue: true
      - key: ENCRYPTION_KEY
        generateValue: true

databases:
  - name: euvatease-db
    databaseName: euvatease
    user: euvatease
```

#### 2. Connectez votre repo GitHub à Render

### Option 3: Docker (Production)

#### Dockerfile

```dockerfile
# backend/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Security: run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml (Production)

```yaml
version: '3.8'

services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=${DATABASE_URL}
      - SHOPIFY_API_KEY=${SHOPIFY_API_KEY}
      - SHOPIFY_API_SECRET=${SHOPIFY_API_SECRET}
      - SHOPIFY_WEBHOOK_SECRET=${SHOPIFY_WEBHOOK_SECRET}
      - SHOPIFY_REDIRECT_URI=${SHOPIFY_REDIRECT_URI}
      - SHOPIFY_APP_URL=${SHOPIFY_APP_URL}
      - JWT_SECRET=${JWT_SECRET}
      - ENCRYPTION_KEY=${ENCRYPTION_KEY}
      - SMTP_HOST=${SMTP_HOST}
      - SMTP_PORT=${SMTP_PORT}
      - SMTP_USERNAME=${SMTP_USERNAME}
      - SMTP_PASSWORD=${SMTP_PASSWORD}
      - FRONTEND_URL=${FRONTEND_URL}
    depends_on:
      - postgres
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: euvatease
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  postgres_data:
```

### Option 4: AWS Elastic Beanstalk

```bash
# Installer EB CLI
pip install awsebcli

# Initialiser
cd backend
eb init -p java-21 euvatease-api

# Créer l'environnement
eb create euvatease-prod

# Configurer les variables
eb setenv \
  DATABASE_URL=jdbc:postgresql://... \
  SHOPIFY_API_KEY=... \
  # ... autres variables

# Déployer
eb deploy
```

---

## ⚛️ Déploiement du Frontend

### Option 1: Vercel (Recommandé)

#### 1. Configuration

Créez `frontend/vercel.json` :

```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "framework": "vite",
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

#### 2. Déployer

```bash
cd frontend
npm i -g vercel
vercel login
vercel

# En production
vercel --prod
```

#### 3. Variables d'environnement

Dans le dashboard Vercel :
- `VITE_API_URL` = `https://api.yourdomain.com/api`
- `VITE_SHOPIFY_API_KEY` = votre clé API Shopify

### Option 2: Netlify

```bash
cd frontend
npm run build

# Installer Netlify CLI
npm i -g netlify-cli
netlify login
netlify deploy --prod --dir=dist
```

### Option 3: Cloudflare Pages

1. Connectez votre repo GitHub
2. Build command: `npm run build`
3. Output directory: `dist`
4. Ajoutez les variables d'environnement

---

## 🛍️ Configuration Shopify

### 1. Créer l'application Shopify

1. Allez sur [Shopify Partners](https://partners.shopify.com/)
2. Apps → Create app → Create app manually
3. Notez :
   - **Client ID** (= `SHOPIFY_API_KEY`)
   - **Client secret** (= `SHOPIFY_API_SECRET`)

### 2. Configuration de l'app

Dans **App setup** :

```
App URL: https://app.yourdomain.com
Allowed redirection URL(s):
  - https://api.yourdomain.com/api/shopify/callback

Embedded app: Yes
```

### 3. Configurer les Webhooks

Dans **Webhooks** :

| Event | URL |
|-------|-----|
| `orders/create` | `https://api.yourdomain.com/api/webhooks/orders/create` |
| `orders/updated` | `https://api.yourdomain.com/api/webhooks/orders/updated` |
| `app/uninstalled` | `https://api.yourdomain.com/api/webhooks/app/uninstalled` |

**Webhook API version**: `2024-01`

Copiez le **Webhook signing secret** → `SHOPIFY_WEBHOOK_SECRET`

### 4. Scopes requis

Dans **API access** → **Configure Admin API scopes** :

- ✅ `read_orders`
- ✅ `read_customers`
- ✅ `read_products`
- ✅ `write_products`

### 5. Extension de l'app (App Bridge)

Créez `shopify.app.toml` à la racine :

```toml
name = "EU VAT Ease"
client_id = "your_client_id"
application_url = "https://app.yourdomain.com"
embedded = true

[auth]
redirect_urls = ["https://api.yourdomain.com/api/shopify/callback"]

[webhooks]
api_version = "2024-01"

[pos]
embedded = false
```

---

## 🌐 Configuration DNS et SSL

### Structure DNS recommandée

| Type | Nom | Valeur |
|------|-----|--------|
| A/CNAME | `api` | IP ou URL du backend |
| A/CNAME | `app` | URL Vercel/Netlify |
| A/CNAME | `@` | Landing page (optionnel) |

### SSL/TLS

- **Railway/Render/Vercel** : SSL automatique ✅
- **Docker auto-hébergé** : Utilisez [Caddy](https://caddyserver.com/) ou [Traefik](https://traefik.io/)

#### Exemple avec Caddy

```
# Caddyfile
api.yourdomain.com {
    reverse_proxy backend:8080
}

app.yourdomain.com {
    reverse_proxy frontend:3000
}
```

---

## 📊 Monitoring et Logs

### Health Check Endpoint

L'application expose `/api/actuator/health` pour les health checks.

### Logging en production

Ajoutez dans `application.yml` :

```yaml
logging:
  level:
    root: WARN
    com.euvatease: INFO
  pattern:
    console: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
```

### Services de monitoring recommandés

| Service | Usage |
|---------|-------|
| [Sentry](https://sentry.io/) | Error tracking |
| [Datadog](https://www.datadoghq.com/) | APM & Logs |
| [UptimeRobot](https://uptimerobot.com/) | Uptime monitoring (gratuit) |
| [LogDNA](https://www.logdna.com/) | Log aggregation |

### Intégration Sentry (optionnel)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-spring-boot-starter-jakarta</artifactId>
    <version>7.3.0</version>
</dependency>
```

```yaml
# application.yml
sentry:
  dsn: ${SENTRY_DSN}
  traces-sample-rate: 0.1
```

---

## ✅ Checklist de production

### Sécurité

- [ ] Toutes les variables d'environnement sont définies
- [ ] JWT_SECRET est unique et >= 256 bits
- [ ] ENCRYPTION_KEY est unique et 32 caractères
- [ ] Base de données avec mot de passe fort
- [ ] HTTPS activé partout
- [ ] CORS configuré correctement
- [ ] Rate limiting activé (optionnel)

### Base de données

- [ ] Backups automatiques configurés
- [ ] Migrations Flyway appliquées
- [ ] Connection pooling configuré
- [ ] SSL pour la connexion DB

### Shopify

- [ ] App URL correcte
- [ ] Redirect URLs configurées
- [ ] Webhooks configurés et testés
- [ ] Scopes API corrects
- [ ] App est en mode "Production" (pas Development)

### Infrastructure

- [ ] Health checks configurés
- [ ] Auto-scaling configuré (si applicable)
- [ ] Logs centralisés
- [ ] Alertes configurées
- [ ] Domaine personnalisé configuré

### Performance

- [ ] Gzip activé
- [ ] Cache headers configurés
- [ ] CDN pour le frontend (Vercel/Netlify = inclus)
- [ ] Database indexes vérifiés

---

## 🔄 CI/CD Pipeline (GitHub Actions)

Créez `.github/workflows/deploy.yml` :

```yaml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Test Backend
        working-directory: ./backend
        run: ./mvnw test
      
      - name: Test Frontend
        working-directory: ./frontend
        run: |
          npm ci
          npm test

  deploy-backend:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Deploy to Railway
        uses: berviantoleo/railway-deploy@main
        with:
          railway_token: ${{ secrets.RAILWAY_TOKEN }}
          service: backend

  deploy-frontend:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          working-directory: ./frontend
```

---

## 🆘 Dépannage

### L'application ne démarre pas

```bash
# Vérifier les logs
railway logs
# ou
docker logs container_name
```

### Erreur de connexion à la base de données

1. Vérifiez `DATABASE_URL`
2. Vérifiez que l'IP est autorisée (whitelist)
3. Vérifiez SSL : ajoutez `?sslmode=require` à l'URL si nécessaire

### Webhooks Shopify ne fonctionnent pas

1. Vérifiez `SHOPIFY_WEBHOOK_SECRET`
2. Testez avec ngrok en local
3. Vérifiez les logs pour les erreurs HMAC

### Erreur CORS

Vérifiez que `FRONTEND_URL` correspond exactement à votre domaine frontend (avec `https://`).

---

## 📞 Support

Pour toute question :
- 📧 Email : support@euvatease.com
- 📖 Documentation : https://docs.euvatease.com
- 🐛 Issues : https://github.com/your-repo/issues

---

**Bon déploiement ! 🚀**
