# Transport & Livraison — Spring Boot 4 (Java 17+)

Plateforme unique de gestion de **courses de taxi** et de **livraisons de colis**.
Cœur du modèle : une entité `Mission` (type `COURSE` ou `LIVRAISON`) partagée par
les chauffeurs/livreurs, les véhicules, le moteur d'attribution, la tarification et les statistiques.

## Lancer le projet (VSCode)

1. Installer : JDK 17 ou 21, Maven (ou utiliser `mvnw` si présent), puis dans VSCode les extensions
   **Extension Pack for Java** et **Spring Boot Extension Pack**.
2. Ouvrir le dossier `transport` (Fichier > Ouvrir un dossier).
3. Terminal : `mvn spring-boot:run` (ou bouton ▶ sur `TransportApplication`).
4. Ouvrir <http://localhost:8081>.

> Si `mvnw` est absent : générer un projet vide avec *Spring Initializr* (Ctrl+Shift+P > "Spring Initializr:
> Create a Maven Project") et copier `mvnw`, `mvnw.cmd` et le dossier `.mvn/` dans ce projet.
> Garder le `pom.xml` de ce projet (version fixée : Spring Boot 4.1.1).

Base de données : **H2 en mémoire** par défaut (données de démo recréées à chaque démarrage).
Pour MySQL : créer la base (ou laisser `createDatabaseIfNotExist`), régler `application-mysql.properties`
puis lancer `mvn spring-boot:run -Dspring-boot.run.profiles=mysql`.

## Comptes de démonstration

| Compte | Mot de passe | Rôle | Droits |
| --- | --- | --- | --- |
| admin | admin123 | ADMIN | tout, y compris Paramètres |
| manager | manager123 | MANAGER | gestion métier + statistiques |
| operator | operator123 | OPERATOR | missions, clients, attribution, changement de statut |
| user | user123 | USER | consultation |

## Architecture

`controller (rest + web) -> service -> service/impl -> repository -> entity`, avec `dto`, `mapper`,
`exception` (handlers REST et web), `security`, `config`, `util`.

## Workflow des missions

`CREEE -> ASSIGNEE -> PRISE_EN_CHARGE -> EN_COURS -> TERMINEE`, états alternatifs `ANNULEE`, `ECHEC`, `RETOURNEE`
(table de transitions dans `StatutMission`). Une transition interdite renvoie HTTP 422.

## Attribution (score)

Éligibles : chauffeur `DISPONIBLE`, permis valide, véhicule `DISPONIBLE`, capacité suffisante, aucune mission active.
`score = coefDistance x facteurPriorite x 1/(1+distance) + coefZone x memeZone + coefCharge x 1/(1+missionsDuJour)`
(coefficients modifiables dans Paramètres). Verrou pessimiste + transaction pour éviter les doubles attributions.

## API REST (HTTP Basic, ex. `-u manager:manager123`)

| Méthode | URL | Rôle |
| --- | --- | --- |
| GET/POST | `/api/chauffeurs`, `/api/vehicules`, `/api/clients` | liste paginée (`page,size,sort,q,statut`) / création |
| GET/PUT/DELETE | `/api/{ressource}/{id}` | détail / modification / suppression |
| PATCH | `/api/chauffeurs/{id}/status`, `/api/vehicules/{id}/status` | changement de statut |
| GET | `/api/chauffeurs/available`, `/api/vehicules/available` | disponibles |
| GET/POST | `/api/missions` | liste filtrée (`type,statut,priorite,chauffeurId,from,to,q`) / création |
| GET | `/api/missions/pending`, `/{id}/candidates`, `/{id}/tracking`, `/{id}/incidents` | |
| POST | `/api/missions/{id}/assign` | corps vide = auto, `{"chauffeurId":3}` = manuel |
| POST | `/api/missions/{id}/pickup`, `/start`, `/complete`, `/cancel`, `/fail`, `/return`, `/incidents` | workflow |
| PUT | `/api/incidents/{id}/resolve` | résolution |
| GET | `/api/statistics/dashboard`, `/revenue`, `/activity` | `from,to,type,chauffeurId,zone` |
| GET/PUT | `/api/parametres`, `/api/parametres/{cle}` | ADMIN pour PUT |

Exemples :

```bash
curl -u manager:manager123 "http://localhost:8081/api/missions?statut=CREEE&sort=datePrevue,desc"
curl -u operator:operator123 -X POST http://localhost:8081/api/missions/6/assign
curl -u operator:operator123 -X POST http://localhost:8081/api/missions/6/pickup
curl -u operator:operator123 -X POST http://localhost:8081/api/missions/6/start
curl -u operator:operator123 -X POST -H "Content-Type: application/json" \
     -d '{"nomReceptionnaire":"Mme Rabe","codeConfirmation":"CONF-1"}' \
   http://localhost:8081/api/missions/7/complete
```

Codes d'erreur : 400 (validation / règle métier), 401/403 (sécurité), 404, 409 (conflit : doublon, chauffeur indisponible…),
422 (transition de statut interdite).
