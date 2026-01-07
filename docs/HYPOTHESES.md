# Hypothèses de conception - EU VAT Ease

Ce document liste toutes les hypothèses faites lors du développement du plugin. Ces choix peuvent être ajustés en fonction des retours utilisateurs.

## 1. Extraction des données TVA

### 1.1 Numéro de TVA B2B

**Hypothèse** : Le numéro de TVA intracommunautaire est recherché dans l'ordre suivant :
1. `order.note` - Recherche par regex des formats TVA européens
2. `order.note_attributes` avec les clés : `vat_number`, `tax_id`, `company_vat`, `tva_number`, `vat_id`
3. `customer.metafields` avec les clés : `vat_number`, `tax_id`

**Justification** : Les marchands Shopify utilisent différentes méthodes pour collecter le numéro de TVA. Cette approche couvre les cas les plus courants.

**Alternative** : Permettre à l'utilisateur de configurer le champ source dans les paramètres.

### 1.2 Taux de TVA appliqué

**Hypothèse** : Le taux de TVA est extrait depuis `order.tax_lines`. Si plusieurs lignes de taxe existent, nous prenons la ligne avec le taux le plus élevé (taux standard).

**Justification** : Les commandes peuvent avoir des taux réduits pour certains produits. Le taux standard est le plus pertinent pour la détection d'erreurs.

### 1.3 Montant de TVA

**Hypothèse** : Si `tax_lines` est vide mais que la commande a un montant total, nous calculons la TVA par déduction : `TVA = total_price - subtotal_price`.

**Justification** : Certaines configurations Shopify n'exposent pas les `tax_lines` correctement.

## 2. Détection B2B vs B2C

### 2.1 Critères B2B

**Hypothèse** : Une commande est considérée B2B si :
- Un numéro de TVA est présent ET
- Ce numéro est validé comme valide via VIES

**Justification** : La simple présence d'un numéro ne suffit pas. Un numéro invalide ne permet pas l'exonération de TVA.

### 2.2 TVA sur ventes B2B

**Hypothèse** : Sur une vente B2B intracommunautaire (client UE hors pays vendeur), la TVA devrait être de 0% (autoliquidation).

**Justification** : C'est la règle standard de TVA intracommunautaire pour les ventes B2B.

## 3. Géolocalisation

### 3.1 Pays de destination

**Hypothèse** : Le pays utilisé pour déterminer le taux de TVA est `shipping_address.country`, pas `billing_address.country`.

**Justification** : Le lieu de livraison détermine le lieu de consommation selon les règles OSS.

### 3.2 Ventes domestiques vs intracommunautaires

**Hypothèse** : Une vente est "domestique" si le pays de livraison = pays d'établissement du vendeur (configuré dans les paramètres).

**Justification** : Les ventes domestiques ne sont pas concernées par l'OSS.

## 4. Seuil OSS

### 4.1 Calcul du seuil

**Hypothèse** : Le seuil de 10 000€ est calculé sur l'année civile, basé sur les ventes B2C livrées dans l'UE (hors pays vendeur).

**Justification** : C'est la définition légale du seuil OSS.

### 4.2 Devises

**Hypothèse** : Pour les ventes en devises autres que l'EUR, nous utilisons le taux de change fourni par Shopify (`presentment_currency` / `shop_currency`) au moment de la commande.

**Justification** : Utiliser le taux au moment de la transaction est plus précis que des taux moyens.

## 5. Service VIES

### 5.1 Indisponibilité

**Hypothèse** : Si le service VIES est indisponible, nous :
1. Stockons la validation en statut "PENDING"
2. Réessayons automatiquement toutes les 15 minutes
3. Maximum 3 tentatives
4. Créons une alerte après 3 échecs

**Justification** : Le service VIES est parfois instable. Un système de retry évite les faux négatifs.

### 5.2 Mise en cache

**Hypothèse** : Les résultats de validation VIES sont mis en cache pendant 24 heures.

**Justification** : Réduire les appels au service VIES et améliorer les performances. Un numéro TVA valide aujourd'hui sera probablement encore valide demain.

## 6. Rapports OSS

### 6.1 Période de rapport

**Hypothèse** : Un rapport OSS couvre un trimestre calendaire complet (T1: Jan-Mar, T2: Avr-Jun, etc.). Les commandes sont attribuées au trimestre selon leur date de création (`created_at`).

**Justification** : C'est la définition standard des trimestres fiscaux.

### 6.2 Commandes incluses

**Hypothèse** : Seules les commandes répondant à TOUS ces critères sont incluses :
- Statut : `paid` ou `fulfilled`
- Type : B2C (pas de numéro TVA validé)
- Destination : Pays UE différent du pays vendeur

**Justification** : L'OSS concerne uniquement les ventes B2C transfrontalières UE.

### 6.3 Remboursements

**Hypothèse** : Les remboursements sont déduits du trimestre où le remboursement a été émis, pas du trimestre de la vente originale.

**Justification** : Simplification comptable alignée sur les pratiques courantes.

## 7. Alertes

### 7.1 Types d'alertes

**Hypothèse** : Les alertes sont générées pour :
- Erreur de taux TVA (différence > 0.5%)
- TVA facturée sur vente B2B validée
- Seuil OSS atteint (80%, 90%, 100%)
- Échéance OSS (7 jours avant, jour J)
- Échec VIES après 3 tentatives

**Justification** : Ces cas représentent les risques de non-conformité les plus courants.

### 7.2 Sévérité

**Hypothèse** :
- `CRITICAL` : Erreur TVA, TVA sur B2B
- `WARNING` : Seuil OSS, échéance proche
- `INFO` : Échec VIES temporaire

**Justification** : Prioriser les alertes qui ont un impact financier ou légal immédiat.

## 8. Abonnement

### 8.1 Période d'essai

**Hypothèse** : L'essai gratuit de 14 jours commence à la première synchronisation de commandes, pas à l'installation.

**Justification** : Donner aux utilisateurs le temps de configurer avant que l'essai commence.

### 8.2 Fin d'abonnement

**Hypothèse** : À l'expiration de l'abonnement, l'accès aux fonctionnalités est bloqué mais les données sont conservées pendant 30 jours.

**Justification** : Permettre aux utilisateurs de réactiver sans perdre leur historique.

## 9. Données et confidentialité

### 9.1 Rétention des données

**Hypothèse** : Les données de commandes sont conservées pendant 7 ans (obligation légale française).

**Justification** : Conformité avec les obligations de conservation des documents comptables.

### 9.2 Suppression de compte

**Hypothèse** : La suppression du compte supprime toutes les données personnelles mais conserve un journal anonymisé des transactions pour les audits.

**Justification** : Équilibre entre droit à l'oubli (RGPD) et obligations légales.

---

## Révisions

| Version | Date | Modifications |
|---------|------|---------------|
| 1.0 | 2024-01-15 | Version initiale |

Ces hypothèses seront révisées en fonction des retours utilisateurs et des évolutions réglementaires.
