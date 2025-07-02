# Klient Wiersza Poleceń (CLI)

## Opis Projektu

Testowa aplikacja CNApp oparta na protokole SSMMP. Interfejsem użytkownika jest klient wiersza poleceń (CLI), który komunikuje się z systemem mikrousług poprzez API Gateway. Żądania i odpowiedzi przesyłane są w formie obiektów klasy String, a cała architektura jest bezstanowa.

## Rola Komponentu

Ten projekt to interfejs użytkownika (CLI) dla całej aplikacji. Komunikuje się on wyłącznie z API Gateway poprzez protokół TCP, wysyłając żądania użytkownika i odbierając odpowiedzi. Umożliwia on m.in. rejestrację, logowanie, wysyłanie/odbieranie postów oraz transfer plików.

## Konfiguracja

Ten komponent wymaga następujących zmiennych środowiskowych w pliku `.env`:

SERVER_ADDRESS=

SERVER_PORT=

## Wymagania

Do poprawnego działania tego komponentu wymagane jest uruchomienie i skonfigurowanie następujących usług:

* **API Gateway**: Komponent komunikuje się z resztą systemu poprzez API Gateway.

## Uruchomienie

Uruchomienie aplikacji odbywa się przy użyciu Dockera.

1.  **Sklonuj repozytorium**
2.  **Skonfiguruj zmienne środowiskowe**: Utwórz plik `.env` w głównym katalogu projektu i uzupełnij go o wymagane wartości (możesz skorzystać z `.env.sample`).
3.  **Uruchom aplikację**: W głównym katalogu projektu wykonaj polecenie:
    ```bash
    docker-compose up --build
    ```
    Spowoduje to zbudowanie obrazu Docker i uruchomienie kontenera z aplikacją.