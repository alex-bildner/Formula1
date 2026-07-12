# Formula 1 App

Aplicativo Android para listar equipes de Formula 1, visualizar detalhes da escuderia e pilotos, com suporte a favoritos e dados offline.

## Funcionalidades

- Listagem de equipes da temporada atual.
- Tela de detalhe da equipe com pilotos.
- Marcar e desmarcar equipes como favoritas.
- Persistencia local com Room para consulta offline.
- Atualizacao de dados pela API publica da Formula 1.

## Stack Tecnologica

- Kotlin
- Android Jetpack Compose (Material 3)
- Navigation Compose
- ViewModel + Coroutines + Flow
- Retrofit + Gson + OkHttp
- Room (SQLite)

## Requisitos

- Android Studio (Hedgehog ou superior recomendado)
- JDK 17
- Android SDK:
  - `compileSdk 34`
  - `minSdk 24`

## Como executar

1. Abra o projeto no Android Studio.
2. Aguarde a sincronizacao do Gradle.
3. Execute o app em um emulador ou dispositivo Android (`Run app`).

## Estrutura resumida

- `app/src/main/java/com/example/formula1/data`: camada de dados (API, banco, repositorio)
- `app/src/main/java/com/example/formula1/domain`: modelos de dominio
- `app/src/main/java/com/example/formula1/ui`: telas, navegacao e ViewModels

## API utilizada

- Base URL: `https://f1api.dev/api/`
- Endpoints principais:
  - `GET current/teams`
  - `GET current/teams/{teamId}/drivers`

## Observacoes

- Arquivos de build e cache devem permanecer fora do versionamento (ver `.gitignore`).
