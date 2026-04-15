# Patient Observation Tracker

[![CI](https://github.com/RohithGowdaD/Project3/actions/workflows/ci.yml/badge.svg)](https://github.com/RohithGowdaD/Project3/actions/workflows/ci.yml)

**CSCI-P532 Object Oriented Software Development — Spring 2026 — Project 3, Week 1**

A lightweight clinical-record system built with Spring Boot 3, Spring Data JPA, SQLite, and plain HTML/JS.  
Inspired by the Observations and Measurements patterns (Fowler, *Analysis Patterns*, Chapter 3) and the Accountability patterns (Chapter 2).

---

## Live Links

### Frontend (GitHub Pages)

| Page | URL |
|------|-----|
| Patients (Home) | https://rohithgowdad.github.io/Project3/index.html |
| Patient Detail | https://rohithgowdad.github.io/Project3/patient.html |
| Catalogue | https://rohithgowdad.github.io/Project3/catalogue.html |
| Logs | https://rohithgowdad.github.io/Project3/logs.html |

### Backend (Render.com)

> Base URL: `https://project3-ywcp.onrender.com`  
> *(Replace with your actual Render URL)*

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | https://your-app-name.onrender.com/api/patients | List all patients |
| POST | https://your-app-name.onrender.com/api/patients | Create patient |
| GET | https://your-app-name.onrender.com/api/patients/{id}/observations | List observations for patient |
| POST | https://your-app-name.onrender.com/api/observations/measurement | Record measurement |
| POST | https://your-app-name.onrender.com/api/observations/category | Record category observation |
| POST | https://your-app-name.onrender.com/api/observations/{id}/reject | Reject observation |
| POST | https://your-app-name.onrender.com/api/patients/{id}/evaluate | Run diagnostic rules |
| GET | https://your-app-name.onrender.com/api/phenomenon-types | List phenomenon types |
| POST | https://your-app-name.onrender.com/api/phenomenon-types | Create phenomenon type |
| POST | https://your-app-name.onrender.com/api/phenomenon-types/phenomena | Add phenomenon to type |
| GET | https://your-app-name.onrender.com/api/protocols | List protocols |
| POST | https://your-app-name.onrender.com/api/protocols | Create protocol |
| GET | https://your-app-name.onrender.com/api/rules | List diagnostic rules |
| POST | https://your-app-name.onrender.com/api/rules | Create diagnostic rule |
| GET | https://your-app-name.onrender.com/api/command-log | View command log |
| GET | https://your-app-name.onrender.com/api/audit-log | View audit log |

---

## Quick Start with Docker

```bash
# 1. Build the image
docker build -t tracker .

# 2. Run (SQLite file persists in ./data/)
docker run -p 8080:8080 -v $(pwd)/data:/app/data tracker

# 3. Open the UI
open http://localhost:8080
```

---

## CI/CD Pipeline

Every push triggers:

```
push to any branch
    └── test job       → runs 28 unit tests

push to main only
    ├── build job      → builds JAR + Docker image
    ├── deploy-frontend → deploys HTML/CSS/JS to GitHub Pages
    └── deploy          → triggers Render.com redeploy (backend)
```

GitHub Secrets required:

| Secret | Value |
|--------|-------|
| `RENDER_DEPLOY_HOOK` | Render deploy hook URL |
| `RENDER_APP_URL` | Render app base URL (no trailing slash) |

---

## Design Patterns

### Strategy — `DiagnosisEngine` + `DiagnosisStrategy`
**Where:** `engine/DiagnosisEngine.java`, `engine/strategy/DiagnosisStrategy.java`, `engine/strategy/SimpleConjunctiveStrategy.java`

The rule-evaluation algorithm is injected into `DiagnosisEngine` as a `DiagnosisStrategy` interface with a single `evaluate(rule, observations)` method. Week 1 ships one concrete strategy — `SimpleConjunctiveStrategy` — which fires a rule only when **all** argument observation concepts are present for the patient. Because the algorithm is behind an interface, Week 2 can introduce `WeightedScoringStrategy` as a second `@Component` without touching `DiagnosisEngine` or any existing class.

---

### Observer — `ObservationEvent` + Spring `@EventListener`
**Where:** `event/ObservationEvent.java`, `event/AuditLogListener.java`, `event/RuleEvaluationListener.java`

`ObservationManager` publishes an `ObservationEvent` via Spring's `ApplicationEventPublisher` whenever an observation is created or rejected. Two listeners react automatically:
- **`AuditLogListener`** — appends an `AuditLogEntry` to the database.
- **`RuleEvaluationListener`** — re-evaluates diagnostic rules for the affected patient and logs any new inferences.

Using Spring's built-in event bus keeps listeners completely decoupled from the manager. Adding a `PropagationListener` in Week 2 requires only a new `@Component` with `@EventListener` — zero changes to existing code.

---

### Factory — `ObservationFactory`
**Where:** `engine/ObservationFactory.java`

All `Observation` objects (`Measurement`, `CategoryObservation`) are created exclusively through `ObservationFactory`. The factory validates that:
- For measurements: the `PhenomenonType` is `QUANTITATIVE` and the unit is in the allowed set.
- For category observations: the `Phenomenon` belongs to a `QUALITATIVE` `PhenomenonType`.

Controllers never call `new Measurement(...)` directly. The manager trusts factory output as valid, keeping its own code focused on orchestration.

---

### Command — `Command` interface + `CommandLog`
**Where:** `engine/command/Command.java`, `engine/command/CommandLog.java`, `engine/command/CreatePatientCommand.java`, `engine/command/RecordObservationCommand.java`, `engine/command/RejectObservationCommand.java`

Every state-changing operation (create patient, record observation, reject observation) is wrapped in a `Command` object with `execute()`. The `CommandLog` service calls `execute()` and then persists a `CommandLogEntry` with the command type, JSON payload, timestamp, and acting user (`"staff"` in Week 1). Payloads are stored as JSON strings so the Week 2 undo path can reconstruct the original request without a schema change. The command log is exposed at `GET /api/command-log`.

---

## Architecture

Four-layer architecture enforced throughout:

| Layer | Stereotype | Responsibility |
|-------|------------|----------------|
| Client | `@RestController` | HTTP only; zero business logic |
| Manager | `@Service` | Orchestrates use-case sequences |
| Engine | `@Service` | Encapsulates replaceable algorithms |
| ResourceAccess | `@Repository` | Atomic business verbs, no SQL in callers |

---

## Render.com Deployment

1. Create a **Web Service** on Render.com.
2. Set environment to **Docker**.
3. Set port to **8080**.
4. Add a **Disk** (1 GB, mount path `/app/data`) so the SQLite file persists between deploys.
5. Copy the deploy-hook URL and add it as a GitHub secret named `RENDER_DEPLOY_HOOK`.
6. Copy the app URL and add it as a GitHub secret named `RENDER_APP_URL`.

---

## Running Tests

```bash
mvn test
```

28 unit tests covering `ObservationFactory`, `SimpleConjunctiveStrategy`, Command objects, and Observer listeners. No `@SpringBootTest` in unit tests.
