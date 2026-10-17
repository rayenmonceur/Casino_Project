# QARTAJ Royal Casino

Application web de casino de démonstration, en **jetons virtuels uniquement** (aucun argent réel, aucun paiement).
Projet réalisé avec Spring Boot et Angular.

## Fonctionnalités

- Inscription / connexion (JWT, mots de passe hashés en BCrypt), rôles USER et ADMIN
- 1000 jetons virtuels offerts à l'inscription
- Jeux : Roulette européenne (0 à 36) et Machine à sous (3 rouleaux)
- Tirages aléatoires avec `SecureRandom` basés sur un système **Provably Fair** (HMAC-SHA256) :
  le joueur peut vérifier chaque résultat
- Tableau de bord admin : transactions (émetteur, récepteur, montant), statistiques,
  alertes de sécurité (échecs de connexion, blocages, dépassement de débit)
- Sécurité : anti brute-force (5 échecs = blocage 15 min), rate limiting (120 requêtes/min/IP),
  verrouillage optimiste du solde (`@Version`), CORS restreint, secrets via variables d'environnement

## Architecture

```
Casino_Project/
├── casino-backend/    Spring Boot 4, Spring Security, JPA, MySQL/MariaDB
│   └── src/main/java/tn/casino/casino_backend/
│       ├── config/ controller/ dto/ entity/ game/ repository/ security/ service/
└── casino-frontend/   Angular (standalone components, signals)
    └── src/app/ { core/ pages/ shared/ }
```

## Prérequis

- Java 17+ (testé avec 25), Node.js 20.19+ ou 22.12+, Angular CLI
- MySQL ou MariaDB (XAMPP convient), base `casino_db` créée automatiquement

## Lancer le projet

Backend :

```bash
cd casino-backend
mvnw spring-boot:run
```

Frontend :

```bash
cd casino-frontend
npm install --legacy-peer-deps
ng serve
```

Ouvrir http://localhost:4200. Compte admin par défaut (dev uniquement) : `admin` / `Admin@12345`.

## Variables d'environnement

| Variable | Rôle | Défaut (dev) |
|---|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Base de données | localhost, root, vide |
| `JWT_SECRET` | Clé de signature des tokens (min. 32 caractères) | valeur de dev |
| `ADMIN_USER`, `ADMIN_PASSWORD`, `ADMIN_EMAIL` | Compte admin initial | admin / Admin@12345 |
| `CORS_ORIGINS` | Origines autorisées (séparées par des virgules) | http://localhost:4200 |
| `PORT` | Port du backend | 8081 |

**En production : changer tous les secrets.**

## API principale

| Méthode | Route | Accès |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | public |
| GET | `/api/wallet/balance` | connecté |
| POST | `/api/games/roulette`, `/api/games/slots` | connecté |
| GET/POST | `/api/fair/current`, `/client-seed`, `/rotate`, `/verify` | connecté |
| GET | `/api/admin/stats`, `/transactions`, `/security-events` | ADMIN |

## Provably Fair

`résultat = HMAC_SHA256(serverSeed, clientSeed:nonce:index)`. Le serveur publie le hash SHA-256 du
`serverSeed` avant de jouer, puis le révèle après rotation. La page `/fair` permet de recalculer
n'importe quel résultat.

## Avertissement

Projet pédagogique. Jetons sans valeur monétaire. Les jeux d'argent réel sont réglementés
(et interdits aux résidents en Tunisie) : ce code ne doit pas être utilisé avec de l'argent réel.