# Klient Wiersza Poleceń (CLI)

## Opis Projektu

Testowa aplikacja CNApp oparta na protokole SSMMP. Interfejsem użytkownika jest klient wiersza poleceń (CLI), który komunikuje się z systemem mikrousług poprzez API Gateway. Żądania i odpowiedzi przesyłane są w formie obiektów klasy String, a cała architektura jest bezstanowa.

## Rola Komponentu

Ten projekt to interfejs użytkownika (CLI) dla całej aplikacji. Komunikuje się on wyłącznie z API Gateway poprzez protokół TCP, wysyłając żądania użytkownika i odbierając odpowiedzi. Umożliwia on m.in. rejestrację, logowanie, wysyłanie/odbieranie postów oraz transfer plików.

## Konfiguracja

Ten komponent wymaga następujących zmiennych środowiskowych w pliku `.env`:

SERVER_ADDRESS=

SERVER_PORT=


## Uruchomienie

Aplikację można uruchomić, wykonując główną metodę `main` w klasie `CLI.java`.