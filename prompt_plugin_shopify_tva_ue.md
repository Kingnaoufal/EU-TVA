x # 🧠 PROMPT IA — Génération d’un Plugin Shopify TVA UE (OSS) + Landing Page

## ⚠️ INSTRUCTIONS CRITIQUES (À LIRE EN PREMIER)

Tu dois **EXÉCUTER STRICTEMENT** ce cahier des charges.
- ❌ Ne pas interpréter
- ❌ Ne pas simplifier
- ❌ Ne pas supprimer de fonctionnalités
- ❌ Ne pas répondre par de la théorie
- ✅ Produire du code fonctionnel
- ✅ Déployer en production
- ✅ Fournir toutes les URLs finales

Si une information est manquante, fais **une hypothèse explicite**, mais **n’interromps jamais l’exécution**.

---

## 🎯 RÔLE DE L’IA

Tu es une **IA développeur senior expert** en :
- Shopify Apps
- SaaS B2B
- TVA européenne / OSS
- Architecture cloud sécurisée
- Déploiement production

Tu agis comme un **lead engineer responsable d’un produit commercial réel**.

---

## 🎯 OBJECTIF PRODUIT

Créer une **application Shopify (plugin)** destinée aux **micro‑business européens** (France en priorité) vendant dans plusieurs pays de l’UE.

### Problème
- TVA UE complexe
- Régime OSS mal compris
- Erreurs Shopify fréquentes
- Tableurs manuels
- Stress du contrôle fiscal

### Solution
Un plugin Shopify qui :
- détecte les erreurs de TVA
- calcule la TVA UE par pays
- génère un **rapport OSS trimestriel prêt à envoyer**
- conserve un historique légal
- alerte automatiquement l’utilisateur

---

## 🧩 POSITIONNEMENT (NON NÉGOCIABLE)

- ❌ Pas enterprise
- ❌ Pas fiscalistes
- ✅ Micro‑business Shopify
- ✅ UX ultra simple
- ✅ Prix clair
- ✅ Objectif : tranquillité d’esprit

---

## 🧱 STACK TECHNIQUE IMPOSÉE

### Shopify
- Shopify Embedded App
- OAuth officiel
- Webhooks (orders, refunds)
- Shopify Billing API

### Backend
- JAVA
- API REST
- PostgreSQL
- Architecture modulaire

### Frontend
- React
- Shopify Polaris

### Infra
- Docker
- HTTPS obligatoire
- Déploiement cloud (AWS / GCP / Railway / Render autorisé)
- Variables d’environnement sécurisées

---

## 🔧 FONCTIONNALITÉS OBLIGATOIRES

### 1. Connexion Shopify
- Installation 1 clic
- Permissions minimales
- Aucune donnée inutile

### 2. Analyse TVA UE
- Analyse ventes par pays UE
- Application des taux TVA
- Gestion régime OSS (simplifié)

> Hypothèse autorisée : pas de cas fiscaux ultra complexes.

### 3. Détection d’erreurs
- Paramétrage Shopify incorrect
- Taux incohérents
- Alertes claires et compréhensibles

### 4. Rapport OSS
- Génération trimestrielle
- Formats : CSV + PDF
- Conforme OSS
- Téléchargeable

### 5. Alertes automatiques
- Email :
  - seuil OSS
  - erreurs
  - échéances

### 6. Historique & audit
- Historique des rapports
- Journal minimal de conformité

### 7. Abonnement
- Plan unique :
  - 99€ / mois
  - 999€ / an
- Via Shopify Billing

### 8. Sécurité & RGPD
- Chiffrement
- Accès restreint
- Données minimales
- RGPD basique respecté

---

## 🧠 UX / COPY (OBLIGATOIRE)

Langage :
- non technique
- rassurant
- orienté stress / tranquillité

Exemples :
- « Tout est prêt pour votre déclaration OSS »
- « Aucune action requise »
- « Vous êtes conforme »

---

## 🌐 LANDING PAGE (À PRODUIRE)

### Objectif
- Conversion
- Accès anticipé
- Pré‑vente

### Contenu obligatoire

#### Hero
**Titre**
> La TVA UE sur Shopify, sans erreurs ni stress.

**Sous‑titre**
> Détectez les erreurs, générez vos rapports OSS et dormez tranquille.

**CTA**
> Rejoindre l’accès anticipé

---

#### Problème
- TVA compliquée
- Excel
- Peur du contrôle
- Temps perdu

---

#### Solution
- Plugin Shopify
- Installation 5 minutes
- Rapports automatiques
- Alertes

---

#### Fonctionnement
1. Connexion Shopify
2. Analyse automatique
3. Rapport OSS prêt

---

#### Pricing
- Comparaison comptable
- 99€/mois ou 999€/an

---

#### CTA final
- Accès anticipé
- Bonus early adopters

---

### Stack landing
- HTML/CSS ou React
- SEO basique
- Formulaire email
- Déploiement public (Netlify / Vercel)

---

## 🚀 DÉPLOIEMENT (OBLIGATOIRE)

Tu dois :
1. Déployer le backend
2. Déployer le frontend
3. Déployer l’app Shopify
4. Fournir les variables d’environnement
5. Fournir les instructions Shopify App Store
6. Déployer la landing page
7. Fournir toutes les URLs finales

---

## 📦 LIVRABLES

Tu dois livrer :
1. Code complet Shopify App
2. Code backend
3. Schéma base de données
4. Code landing page
5. Scripts de déploiement
6. Documentation d’installation
7. Instructions App Store
8. Liste des hypothèses prises

---

## 🏁 DÉFINITION DU SUCCÈS

- App installable sur Shopify
- Rapport OSS générable
- Landing page en ligne
- Produit prêt à être vendu


---

## ➕ FEATURE OBLIGATOIRE SUPPLÉMENTAIRE — VALIDATION TVA INTRACOMMUNAUTAIRE (VIES)

⚠️ **CETTE FEATURE EST OBLIGATOIRE ET DOIT ÊTRE IMPLÉMENTÉE EN CODE FONCTIONNEL.**
L’IA NE DOIT PAS L’IGNORER, NI LA SIMPLIFIER.

### Objectif
Permettre la **validation officielle des numéros de TVA intracommunautaire** des clients B2B via le **service européen officiel VIES**, afin de :
- distinguer B2B / B2C
- justifier les exonérations de TVA
- fournir une **preuve légale horodatée** en cas de contrôle fiscal
- enrichir correctement le **reporting OSS**

### Exigences techniques STRICTES

- Utiliser le **service officiel VIES** (SOAP ou API équivalente disponible)
- Implémenter une couche backend dédiée `vat_validation`
- Vérifier pour chaque numéro :
  - format
  - pays
  - statut (valide / invalide / indisponible)
- Gérer les cas d’erreur VIES (timeout, indisponibilité)
- Mettre en place un **retry contrôlé**
- Ne jamais bloquer le checkout Shopify

### Stockage & preuve
- Stocker en base :
  - numéro de TVA
  - pays
  - résultat de validation
  - date et heure de validation
  - identifiant de commande Shopify associé
- Ces données servent de **preuve légale** et doivent être conservées

### Intégration fonctionnelle
- La validation TVA :
  - n’est PAS une simple option
  - alimente le moteur de calcul TVA
  - impacte le rapport OSS
- Elle ne remplace PAS toute la logique fiscale avancée B2B, mais doit être suffisante pour :
  - distinguer ventes exonérées
  - justifier les lignes OSS

### UX OBLIGATOIRE
Interface claire et non technique :
- ✅ « Numéro de TVA valide (vérifié le JJ/MM/AAAA) »
- ⚠️ « Numéro de TVA invalide ou non vérifiable »
- ❌ « Numéro de TVA manquant »

Aucun jargon fiscal avancé ne doit apparaître côté utilisateur.

---

⚠️ **SI CETTE FEATURE N’EST PAS IMPLÉMENTÉE, LE TRAVAIL EST CONSIDÉRÉ COMME INCOMPLE.**
