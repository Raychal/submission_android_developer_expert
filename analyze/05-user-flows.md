# 5. User Flows

## 5.1 App startup

```mermaid
sequenceDiagram
    participant OS as Android OS
    participant App as MyApplication
    participant Koin
    participant MA as MainActivity
    participant HVM as HomeViewModel

    OS->>App: onCreate()
    App->>App: System.loadLibrary("sqlcipher")
    App->>Koin: startKoin(database, network, repository,<br/>useCase, navigation, viewModel modules)
    OS->>MA: onCreate()
    MA->>MA: enableEdgeToEdge()
    MA->>MA: setContent { Theme { Scaffold + NavHost(start = Screen.Home) } }
    MA->>HVM: koinViewModel()
    HVM->>HVM: init → handleIntent(GetGenres)
    MA->>MA: LaunchedEffect: collect NavigationManager.navigationCommands
```

## 5.2 Navigation map

```mermaid
stateDiagram-v2
    [*] --> Home
    Home --> Detail: tap GameItem<br/>navigate(Screen.Detail(id))
    Detail --> Home: back / ← <br/>navigateUp()
    Home --> FavoriteActivity: tap ♡ FAB<br/>navigate(Screen.Favorite)<br/>→ split install → deep link
    FavoriteActivity --> Home: ← / back (finish)
    FavoriteActivity --> Detail: tap item<br/>navigate(Screen.Detail(id)) + finish()

    state "MainActivity NavHost" as NH {
        Home
        Detail
    }
```

Routes are type-safe `@Serializable` objects in `core.navigation.Screen`: `Home`, `Detail(gameId: Int)`, and `Favorite`. `Favorite` isn't registered in the `NavHost`. It is handled as a special case.

## 5.3 Browse, search, and filter games

```mermaid
sequenceDiagram
    actor U as User
    participant TB as TopAppBar search field (MainActivity)
    participant HS as HomeScreen
    participant VM as HomeViewModel
    participant UC as GameUseCase
    participant R as GameRepository
    participant API as RAWG

    U->>HS: opens app
    HS->>VM: collect gamesPagingData
    VM->>UC: getAllGame(null, null)
    UC->>R: getAllGame
    R->>API: GET /games?page=1&page_size=60 (initial load = 3×pageSize)
    API-->>HS: page 1 → grid renders

    U->>TB: types "witcher"
    TB->>VM: sendIntent(SearchGames("witcher")) on every keystroke
    Note over VM: debounce 500 ms → flatMapLatest cancels old pager
    VM->>UC: getAllGame("witcher", null)
    UC->>R: getAllGame
    R->>API: GET /games?search=witcher&page=1
    API-->>HS: new results

    U->>HS: taps genre chip "Action"
    HS->>VM: sendIntent(SelectGenre("4"))
    VM->>UC: getAllGame("witcher", "4")
    R->>API: GET /games?search=witcher&genres=4

    U->>HS: pull to refresh
    HS->>VM: sendIntent(Refresh()) → clears search & genre
    HS->>HS: games.refresh()
```

> Pull-to-refresh clears the ViewModel's search query, but the `searchQuery` text in `MainActivity` is separate `remember` state. So the text field keeps showing the old text (see [findings](09-findings-and-recommendations.md)).

## 5.4 Open game detail

```mermaid
sequenceDiagram
    actor U as User
    participant HS as HomeScreen
    participant NM as NavigationManager
    participant MA as MainActivity
    participant DS as DetailScreen
    participant VM as DetailViewModel
    participant R as GameRepository
    participant API as RAWG
    participant DB as Room (encrypted)

    U->>HS: tap GameItem
    HS->>NM: navigate(Screen.Detail(id))
    NM-->>MA: NavigationCommand.Navigate
    MA->>DS: navController.navigate(Screen.Detail(id))
    DS->>VM: sendIntent(GetDetail(id))
    VM->>R: getDetailGame(id)
    R-->>VM: Resource.Loading
    VM-->>DS: isLoading = true → shimmer
    R->>API: GET /games/{id}
    R->>API: GET /games/{id}/screenshots
    R->>API: GET /games/{id}/movies
    R->>DB: getGameById(id).first()
    R-->>VM: Resource.Success(game with isFavorite merged)
    VM-->>DS: game = ... → GameDetailContent
    DS->>DS: trailer autoplays if network Available
```

## 5.5 Toggle favorite

```mermaid
sequenceDiagram
    actor U as User
    participant DS as DetailScreen FAB
    participant VM as DetailViewModel
    participant R as GameRepository
    participant DB as Room

    U->>DS: tap ♡
    DS->>VM: sendIntent(ToggleFavorite(game))
    VM->>R: setFavoriteGame(game, true)
    R->>DB: insertSingleGame(entity, isFavorite=true)  [REPLACE]
    VM-->>DS: game.isFavorite = true → red ♥

    U->>DS: tap ♥ again
    DS->>VM: sendIntent(ToggleFavorite(game))
    VM->>R: setFavoriteGame(game, false)
    R->>DB: deleteGame(entity)
    VM-->>DS: game.isFavorite = false → outline ♡
```

## 5.6 Open Favorites (dynamic feature delivery)

```mermaid
sequenceDiagram
    actor U as User
    participant MA as MainActivity
    participant NM as NavigationManager
    participant SIM as SplitInstallManager
    participant FA as FavoriteActivity (:favorite)
    participant FVM as FavoriteViewModel
    participant DB as Room

    U->>MA: tap ♡ FAB (Home)
    MA->>NM: navigate(Screen.Favorite)
    NM-->>MA: Navigate(Screen.Favorite)
    MA->>SIM: installedModules.contains("favorite")?
    alt already installed (install-time delivery / fused)
        MA->>FA: startActivity(ACTION_VIEW "submissionandroiddeveloperexpert://favorites")
        MA->>U: Toast "Open module"
    else not installed
        MA->>SIM: startInstall(request "favorite")
        SIM-->>MA: success → Toast + open deep link
        SIM-->>MA: failure → Toast "Error installing module"
    end
    FA->>FA: SplitCompat.installActivity()
    FA->>FA: loadKoinModules(favoriteModule)
    FA->>FVM: sendIntent(GetFavorites)
    FVM->>DB: getFavoriteGame() (Flow)
    DB-->>FA: list → LazyColumn or Lottie empty state
```

The `favorite` module is configured with `<dist:install-time/>` and `<dist:fusing dist:include="true"/>`. In practice it ships with the base install, so the "already installed" branch is the one that normally runs.

## 5.7 Favorite → Detail (cross-activity navigation)

```mermaid
sequenceDiagram
    actor U as User
    participant FA as FavoriteActivity
    participant NM as NavigationManager (singleton)
    participant MA as MainActivity

    U->>FA: tap favorite item
    FA->>NM: navigate(Screen.Detail(id))
    Note over NM: Command is buffered in Channel
    FA->>FA: finish()
    MA->>NM: (resumes, collector receives command)
    MA->>MA: navController.navigate(Screen.Detail(id))
```

## 5.8 Offline behaviour summary

| Situation | Result |
|---|---|
| Home first load offline | Lottie `no_connection` + "You Are Offline" |
| Home scrolling offline (append fails) | Toast "You Are Offline". Already loaded items stay |
| Server error on Home | Lottie + "Server Error" |
| Detail offline, game **is** favorited | Shown from the local DB (screenshots and movie URLs included, but images and video need network) |
| Detail offline, game not favorited | Error "Offline: Data not found locally." + Retry |
| Trailer offline | Static preview image instead of ExoPlayer |
| Favorites offline | Works fully (local DB). Images depend on Coil's cache |
