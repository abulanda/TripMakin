# TripMakin

## Opis projektu

TripMakin to aplikacja webowa wspierająca organizację i zarządzanie wspólnymi wycieczkami. Umożliwia tworzenie wycieczek, zapraszanie uczestników, planowanie harmonogramu, dzielenie wydatków oraz komunikację między uczestnikami. System posiada panel administracyjny do zarządzania użytkownikami i wycieczkami.

### Kluczowe funkcjonalności

- Rejestracja i logowanie użytkowników
- Tworzenie i edycja wycieczek
- Zarządzanie uczestnikami (zapraszanie, akceptacja zaproszeń)
- Harmonogram wycieczki (dodawanie punktów planu)
- Zarządzanie wydatkami
- Panel administratora (zarządzanie użytkownikami i wycieczkami)
- Powiadomienia o zaproszeniach i statusach wycieczek

## Schemat architektury

```mermaid
flowchart TD
    D["Przeglądarka użytkownika"] --> A["Frontend (React + Vite)"]
    A -->|proxy /api| P["Proxy (Vite)"]
    P -->|REST API| B["Backend (Spring Boot)"]
    B -->|JDBC| C["PostgreSQL"]
    B --> S["Swagger UI"]
    A -->|JWT token| B
```

- **Frontend**: SPA w React, komunikacja z backendem przez REST API.
- **Backend**: Spring Boot, obsługa logiki biznesowej, autoryzacja, komunikacja z bazą danych.
- **Baza danych**: Przechowywanie użytkowników, wycieczek, wydatków itd.

## Instrukcja uruchomienia

### Wymagania wstępne

- Node.js (zalecana wersja 18+)
- Java 17+
- Maven
- Docker (opcjonalnie, jeśli chcesz uruchomić przez docker-compose)

### Uruchomienie backendu

1. Przejdź do katalogu `backend`:
   ```sh
   cd backend
   ```
2. Zbuduj projekt:
   ```sh
   ./mvnw clean package
   ```
3. Uruchom backend:
   ```sh
   ./mvnw spring-boot:run
   ```
   Backend domyślnie nasłuchuje na porcie `8081`.

### Uruchomienie frontendu

1. Przejdź do katalogu `frontend`:
   ```sh
   cd frontend
   ```
2. Zainstaluj zależności:
   ```sh
   npm install
   ```
3. Uruchom frontend:
   ```sh
   npm run dev
   ```
   Frontend będzie dostępny pod adresem `http://localhost:5173`.

### Uruchomienie całości przez Docker Compose

1. W katalogu głównym projektu uruchom:
   ```sh
   docker-compose up --build
   ```
   (Upewnij się, że masz skonfigurowane obrazy dla backendu i frontendu w pliku `docker-compose.yml`.)

## Użyte technologie i uzasadnienie wyboru

- **React 18 + Vite**  
  Nowoczesny framework do budowy interfejsów użytkownika. React 18 zapewnia wysoką wydajność, wsparcie dla hooków i nowoczesnych wzorców projektowych. Vite umożliwia błyskawiczny start projektu i szybki hot-reload podczas developmentu.

- **Spring Boot (Java 17+)**  
  Popularny, nowoczesny framework backendowy, zapewniający szybkie tworzenie aplikacji REST, bezpieczeństwo oraz integrację z bazami danych. Wspiera ORM (Hibernate/JPA) i automatyczną dokumentację API (Swagger).

- **React Query**  
  Nowoczesne narzędzie do zarządzania stanem zapytań HTTP w React. Ułatwia cachowanie, synchronizację i automatyczne odświeżanie danych z backendu.

- **Maven**  
  Standardowy system budowania projektów Java, automatyzujący zarządzanie zależnościami i procesem budowania.

- **Docker**  
  Umożliwia uruchamianie aplikacji w izolowanych kontenerach, co upraszcza wdrożenie i testowanie.

- **PostgreSQL**  
  Stabilna, nowoczesna relacyjna baza danych, dobrze wspierana przez Spring Boot i ORM.

- **Swagger/OpenAPI**  
  Automatyczna, interaktywna dokumentacja REST API, ułatwiająca testowanie i integrację.

**Dlaczego te technologie?**  
Wszystkie wybrane narzędzia i frameworki są aktualne, szeroko wykorzystywane w branży i zapewniają wysoką wydajność, bezpieczeństwo oraz łatwość rozwoju i utrzymania projektu. Dodatkowe narzędzia jak React Query czy Docker podnoszą jakość developmentu i wdrożenia.

---

Projekt zrealizowany na potrzeby zaliczenia przedmiotu Zaawansowane Technologie Programowania Aplikacji Internetowych