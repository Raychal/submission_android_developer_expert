# 6. Dependency Injection (Koin)

## 6.1 Modules

| Koin module | File | Definitions |
|---|---|---|
| `databaseModule` | `core/di/CoreModule.kt` | `single` GameDatabase (Room + SQLCipher factory), `factory` GameDao |
| `networkModule` | `core/di/CoreModule.kt` | `single<NetworkObserver>` ConnectivityObserver, `single` OkHttpClient, `single` ApiService (Retrofit) |
| `repositoryModule` | `core/di/CoreModule.kt` | `single<IGameRepository>` GameRepository |
| `useCaseModule` | `core/di/CoreModule.kt` | `factory<GameUseCase>` GameInteractor |
| `navigationModule` | `core/di/CoreModule.kt` | `single` NavigationManager |
| `viewModelModule` | `app/di/AppModule.kt` | `viewModelOf(::HomeViewModel)`, `viewModelOf(::DetailViewModel)` |
| `favoriteModule` | `favorite/ui/FavoriteModule.kt` | `factory` FavoriteViewModel. Loaded lazily with `loadKoinModules` in `FavoriteActivity.onCreate` |

The first six are started in `MyApplication.onCreate()` with `startKoin { androidContext(...) }`.

## 6.2 Object graph

```mermaid
flowchart BT
    subgraph singletons["single"]
        DB["GameDatabase<br/>(Room + SupportOpenHelperFactory)"]
        OK["OkHttpClient<br/>logging · api-key · pinning"]
        API["ApiService (Retrofit)"]
        NO["NetworkObserver<br/>(ConnectivityObserver)"]
        REPO["IGameRepository<br/>(GameRepository)"]
        NM["NavigationManager"]
    end
    subgraph factories["factory"]
        DAO["GameDao"]
        UC["GameUseCase<br/>(GameInteractor)"]
        FVM["FavoriteViewModel"]
    end
    subgraph viewmodels["viewModel"]
        HVM["HomeViewModel"]
        DVM["DetailViewModel"]
    end

    DAO --> DB
    API --> OK
    REPO --> API
    REPO --> DAO
    UC --> REPO
    HVM --> UC
    HVM --> NO
    DVM --> UC
    DVM --> NO
    FVM --> UC
    FVM --> NO
```

## 6.3 Injection sites

| Consumer | How |
|---|---|
| `MainActivity` | `private val navigationManager: NavigationManager by inject()`; `koinViewModel<HomeViewModel>()` |
| `HomeScreen` | `viewModel = koinViewModel()`, `navigationManager = koinInject()` (both default params) |
| `DetailScreen` | `viewModel = koinViewModel()`, `navigationManager = koinInject()` |
| `FavoriteActivity` | `koinInject<NavigationManager>()` |
| `FavoriteScreen` | `viewModel = koinViewModel()` |

> `FavoriteViewModel` is registered as a plain `factory`, not with `viewModel`/`viewModelOf`. `koinViewModel()` can still resolve it, but registering it with `viewModelOf(::FavoriteViewModel)` would match the other ViewModels. Also, calling `loadKoinModules` on every `onCreate` (for example, after a rotation) re-registers the module. Koin tolerates this, but it's worth knowing.
