# Klient Wiersza Poleceń (CLI)

## Opis Projektu

Testowa aplikacja CNApp oparta na protokole SSMMP. Interfejsem użytkownika jest klient wiersza poleceń (CLI), który komunikuje się z systemem mikrousług poprzez API Gateway. Żądania i odpowiedzi przesyłane są w formie obiektów klasy String, a cała architektura jest bezstanowa.

## Rola Komponentu

Ten projekt to interfejs użytkownika (CLI) dla całej aplikacji. Komunikuje się on wyłącznie z API Gateway poprzez protokół TCP, wysyłając żądania użytkownika i odbierając odpowiedzi. Umożliwia on m.in. rejestrację, logowanie, wysyłanie/odbieranie postów oraz transfer plików.

## Konfiguracja

Ten komponent wymaga następujących zmiennych środowiskowych w pliku `.env`:
```ini
SERVER_ADDRESS=
SERVER_PORT=
```
## Wymagania

Do poprawnego działania tego komponentu wymagane jest uruchomienie i skonfigurowanie następujących usług:

* **API Gateway**: Komponent komunikuje się z resztą systemu poprzez API Gateway.

## Uruchomienie

### Uruchomienie deweloperskie (lokalne)

Ta metoda jest przeznaczona do celów deweloperskich i buduje obraz lokalnie.

1.  **Sklonuj repozytorium**.
2.  **Skonfiguruj zmienne środowiskowe**: Utwórz plik `.env` w głównym katalogu projektu i uzupełnij go o wymagane wartości (możesz skorzystać z `.env.sample`).
3.  **Uruchom aplikację**: W głównym katalogu projektu wykonaj polecenie:
    ```bash
    docker compose up --build
    ```
    Spowoduje to zbudowanie obrazu Docker i uruchomienie kontenera z aplikacją.

### Uruchomienie produkcyjne (z Docker Hub)

Ta metoda wykorzystuje gotowy obraz z repozytorium Docker Hub.

1.  **Pobierz obraz**: Na serwerze docelowym wykonaj polecenie, aby pobrać najnowszą wersję obrazu z repozytorium na Docker Hub.
    ```bash
    docker pull lw89233/cli:latest
    ```

2.  **Przygotuj pliki konfiguracyjne**: W jednym katalogu na serwerze umieść:
    * Uzupełniony plik `.env`.
    * Plik `docker-compose.prod.yml` o następującej treści:
        ```yaml
        services:
          cli:
            image: lw89233/cli:latest
            container_name: cli-service
            stdin_open: true
            tty: true
            restart: unless-stopped
            env_file:
              - .env
        ```

3.  **Uruchom kontener**: W katalogu, w którym znajdują się pliki konfiguracyjne, wykonaj polecenie:
    ```bash
    docker compose -f docker-compose.prod.yml up -d
    ```
    Aplikacja zostanie uruchomiona w tle.