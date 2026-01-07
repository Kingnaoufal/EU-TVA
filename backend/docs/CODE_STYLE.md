# Guide de Style Java - EU VAT Ease

Ce document définit les règles de formatage et de style du code Java pour le projet EU VAT Ease.
Ces règles sont inspirées de Jalopy et garantissent une cohérence maximale du code.

## Table des Matières

1. [Règles de Sections](#règles-de-sections)
2. [Ordre des Membres](#ordre-des-membres)
3. [Null-Safety](#null-safety)
4. [Imports](#imports)
5. [Formatage des Méthodes](#formatage-des-méthodes)
6. [Constructeurs](#constructeurs)
7. [Builders](#builders)

---

## Règles de Sections

Chaque classe Java contient les sections suivantes **uniquement si elles ont du contenu**. Les sections vides ne doivent PAS être insérées.

### Format Exact des Commentaires de Section

```java
//~ ----------------------------------------------------------------------------------------------------------------
//~ Static fields/initializers
//~ ----------------------------------------------------------------------------------------------------------------

//~ ----------------------------------------------------------------------------------------------------------------
//~ Instance fields
//~ ----------------------------------------------------------------------------------------------------------------

//~ ----------------------------------------------------------------------------------------------------------------
//~ Constructors
//~ ----------------------------------------------------------------------------------------------------------------

//~ ----------------------------------------------------------------------------------------------------------------
//~ Methods
//~ ----------------------------------------------------------------------------------------------------------------

//~ ----------------------------------------------------------------------------------------------------------------
//~ Nested Classes
//~ ----------------------------------------------------------------------------------------------------------------
```

> ⚠️ **Important** : Le séparateur contient exactement **112 tirets** (`-`).

---

## Ordre des Membres

### Champs Statiques

Ordre de tri :
1. `public static final`
2. `public static`
3. `protected static final`
4. `protected static`
5. `static final` (package-private)
6. `static` (package-private)
7. `private static final`
8. `private static`

Dans chaque groupe : **ordre alphabétique**.

### Champs d'Instance

Ordre de tri :
1. `public final`
2. `public`
3. `protected final`
4. `protected`
5. `final` (package-private)
6. (package-private)
7. `private final`
8. `private`

Dans chaque groupe : **ordre alphabétique**.

### Constructeurs

Ordre de tri :
1. `public`
2. `protected`
3. (package-private)
4. `private`

### Méthodes

Ordre de tri strict :
1. `public static`
2. `public`
3. `protected`
4. (package-private) `static`
5. (package-private)
6. `private static`
7. `private`

Dans chaque groupe : **ordre alphabétique** par nom, puis par nombre de paramètres.

### Classes Imbriquées (Nested Classes)

Ordre de tri :
1. `public static`
2. `public`
3. `protected static`
4. `protected`
5. (package-private) `static`
6. (package-private)
7. `private static`
8. `private`

---

## Null-Safety

### Annotations Obligatoires

Utiliser **uniquement** les annotations de `jakarta.annotation` :
- `@Nonnull` - La valeur ne peut jamais être null
- `@Nullable` - La valeur peut être null

### Règles d'Application

| Élément | Annotation Requise |
|---------|-------------------|
| Champs d'instance | ✅ Obligatoire |
| Paramètres public/protected | ✅ Obligatoire |
| Paramètres private | ⚡ Recommandé |
| Valeurs de retour (non primitives) | ✅ Obligatoire |
| Types primitifs (int, boolean, etc.) | ❌ Non applicable |

### Exemple

```java
@Nullable
private String name;

@Nonnull
private Shop shop;

@Nullable
public String getName() {
    return name;
}

public void setName(@Nullable String name) {
    this.name = name;
}
```

---

## Imports

### Règles

1. **Pas de wildcard** : `import java.util.*;` ❌
2. **Imports explicites** : `import java.util.List;` ✅
3. **Ordre** :
   - `jakarta.*`
   - `java.*`
   - Autres packages (org.*, com.*, etc.)
4. **Supprimer les imports inutiles**

### Exemple

```java
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
```

---

## Formatage des Méthodes

### Getters et Setters

**Multi-lignes obligatoire** (pas de one-liners) :

```java
// ❌ Incorrect
public Long getId() { return id; }

// ✅ Correct
@Nullable
public Long getId() {
    return id;
}

public void setId(@Nullable Long id) {
    this.id = id;
}
```

### Paramètres Multi-lignes

Si une méthode a plusieurs paramètres qui ne tiennent pas sur une ligne :

```java
public void processOrder(@Nonnull Shop shop,
                         @Nonnull Order order,
                         @Nullable String notes,
                         @Nonnull LocalDateTime timestamp) {
    // ...
}
```

---

## Constructeurs

### Validation des Paramètres @Nonnull

Pour chaque paramètre annoté `@Nonnull`, utiliser `Objects.requireNonNull` :

```java
public MyService(@Nonnull ShopRepository shopRepository,
                 @Nonnull OrderRepository orderRepository,
                 @Nullable EmailService emailService) {
    this.shopRepository = Objects.requireNonNull(shopRepository, "shopRepository must not be null");
    this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
    this.emailService = emailService; // Nullable, pas de validation
}
```

> ⚠️ Ne PAS utiliser `Objects.requireNonNull` sur les primitives (int, boolean, etc.).

### Ordre des Constructeurs

1. Constructeur par défaut (no-args) en premier
2. Constructeur complet (all-args) ensuite

---

## Builders

### Structure

Les builders sont des **classes imbriquées** placées dans la section `Nested Classes`.

```java
//~ ----------------------------------------------------------------------------------------------------------------
//~ Nested Classes
//~ ----------------------------------------------------------------------------------------------------------------

public static class OrderBuilder {

    @Nullable
    private Long id;

    @Nullable
    private Shop shop;

    // ... autres champs

    @Nonnull
    public OrderBuilder id(@Nullable Long id) {
        this.id = id;
        return this;
    }

    @Nonnull
    public OrderBuilder shop(@Nullable Shop shop) {
        this.shop = shop;
        return this;
    }

    @Nonnull
    public Order build() {
        return new Order(id, shop, ...);
    }
}
```

### Méthode Factory

```java
@Nonnull
public static OrderBuilder builder() {
    return new OrderBuilder();
}
```

---

## Enums

Les enums sont placés dans la section `Nested Classes`, avant les builders.

```java
//~ ----------------------------------------------------------------------------------------------------------------
//~ Nested Classes
//~ ----------------------------------------------------------------------------------------------------------------

public enum Status {
    PENDING,
    ACTIVE,
    CANCELLED
}

public static class MyBuilder {
    // ...
}
```

---

## Checklist de Validation

Avant de committer du code, vérifier :

- [ ] Les 5 sections Jalopy sont présentes
- [ ] Les imports sont sans wildcard et ordonnés
- [ ] Tous les champs d'instance ont `@Nonnull` ou `@Nullable`
- [ ] Tous les paramètres public/protected ont des annotations
- [ ] Les constructeurs utilisent `Objects.requireNonNull` pour les `@Nonnull`
- [ ] Les getters/setters sont multi-lignes
- [ ] Les méthodes sont ordonnées selon les règles de visibilité
- [ ] Le code compile sans erreur

---

## Outils

### Vérification Manuelle

Rechercher les patterns suivants pour vérifier la conformité :

```bash
# Imports wildcard (à éliminer)
grep -r "import .*\.\*;" src/main/java/

# Getters inline (à reformater)
grep -r "get.*() { return" src/main/java/
```

### IDE Configuration

Pour IntelliJ IDEA :
1. Settings → Editor → Code Style → Java
2. Importer le fichier de configuration fourni (si disponible)

---

## Historique des Modifications

| Date | Version | Auteur | Description |
|------|---------|--------|-------------|
| 2026-01-07 | 1.0.0 | Équipe Dev | Création initiale |

---

*Ce guide est maintenu par l'équipe de développement EU VAT Ease.*
