# Examen Design Pattern L3 S2 2026 — BadWallet

Deux applications Spring Boot communicantes :

| Application       | Port | Role                                                        |
|-------------------|------|-------------------------------------------------------------|
| `badwallet-api`   | 8080 | Portefeuille electronique (wallets, transactions, paiement) |
| `payment-service` | 8081 | Service externe de facturation (ISM, WOYAFAL)               |

`badwallet-api` appelle `payment-service` via un **Proxy HTTP** pour consulter et payer les factures.

---

## 1. Pre-requis

- Java 17 ou plus
- Maven (ou l'IDE IntelliJ / VS Code avec support Spring Boot)
- Aucune base de donnees a installer : les deux services utilisent **H2 en memoire**.

---

## 2. Lancement (ORDRE IMPORTANT)

Ouvrir **deux terminaux**.

**Terminal 1 — payment-service (port 8081), a demarrer en premier :**
```bash
cd payment-service
mvn spring-boot:run
```
Au demarrage, il genere automatiquement les factures (ISM, WOYAFAL) pour les wallets `WLT-0000001` a `WLT-0000010`.

**Terminal 2 — badwallet-api (port 8080) :**
```bash
cd badwallet-api
mvn spring-boot:run
```

---

## 3. Tests

Ouvrir `test.http` dans VS Code avec l'extension **REST Client**, puis cliquer sur **Send Request**.

> Commencer par la requete **1.1 (seed)** : elle cree les portefeuilles
> `+221770000001` a `+221770000010` (codes `WLT-0000001` a `WLT-0000010`),
> qui correspondent aux numeros utilises dans tout le fichier de test.

Consoles H2 (visualiser les donnees) :
- badwallet-api : http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:walletdb`
- payment-service : http://localhost:8081/h2-console — JDBC URL `jdbc:h2:mem:paymentdb`
- user `sa`, mot de passe vide.

---

## 4. Design Patterns utilises

| Pattern               | Ou                                                      | Pourquoi                                                                 |
|-----------------------|---------------------------------------------------------|--------------------------------------------------------------------------|
| **Strategy**          | `service/strategy/` (DepositStrategy, FeeStrategy)      | Choisir le traitement selon le moyen de depot (CREDIT_CARD / WALLET_TARGET) et le calcul des frais de retrait (1% plafonne a 5000). Ajouter un moyen = ajouter une classe, sans toucher au service. |
| **Factory Method**    | `service/factory/TransactionFactory`                    | Centraliser la creation des objets `Transaction` (depot, retrait, transfert, paiement) au lieu de la dupliquer dans le service. |
| **Proxy (Remote)**    | `proxy/PaymentServiceProxy` implements `FacturationService` | `badwallet-api` ne connait qu'une interface locale ; le proxy delegue les appels reels en HTTP vers `payment-service`. |
| **Repository**        | `repository/`                                           | Abstraction de l'acces aux donnees (Spring Data JPA).                    |
| **DTO**               | `dto/`                                                  | Decoupler le modele interne des donnees exposees par l'API.             |
| **Builder**           | entites + DTO (`@Builder` Lombok)                       | Construction lisible et immuable des objets.                            |

---

## 5. Endpoints

### badwallet-api (8080)
- `POST /api/wallets/seed` — seed async
- `POST /api/wallets` — creer un portefeuille
- `GET  /api/wallets?page=&size=` — liste paginee
- `GET  /api/wallets/{phone}` — consulter
- `GET  /api/wallets/{phone}/balance` — solde
- `POST /api/wallets/{id}/deposit` — depot (Strategy)
- `POST /api/wallets/withdraw` — retrait (frais Strategy)
- `POST /api/wallets/transfer` — transfert
- `POST /api/wallets/pay` — payer factures du mois (montant)
- `POST /api/wallets/pay-factures` — payer factures specifiques
- `GET  /api/wallets/{phone}/transactions` — historique
- `GET  /api/external/factures/{walletCode}/current[?unite=]` — Proxy
- `GET  /api/external/factures/{walletCode}/periode?debut=&fin=` — Proxy

### payment-service (8081)
- `GET  /api/factures/{walletCode}/current[?unite=]`
- `GET  /api/factures/{walletCode}/periode?debut=&fin=`
- `POST /api/factures/pay`
