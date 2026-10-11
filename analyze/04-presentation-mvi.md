# 4. Presentation Layer (MVI + Compose)

## 4.1 MVI base

```kotlin
abstract class BaseViewModel<S : UiState, I : UiIntent>(initialState: S, networkObserver: NetworkObserver) : ViewModel() {
    val state: StateFlow<S>                      // single source of truth for the UI
    val networkStatus: StateFlow<NetworkObserver.Status>
    fun sendIntent(intent: I)                    // UI → VM (emits into MutableSharedFlow)
    protected abstract fun handleIntent(intent: I)
    protected fun setState(reducer: S.() -> S)   // atomic MutableStateFlow.update
}
```

```mermaid
flowchart LR
    UI["Composable (View)"] -- "sendIntent(Intent)" --> SF["MutableSharedFlow&lt;I&gt;"]
    SF -- "onEach → handleIntent()" --> VM["ViewModel logic"]
    VM -- "calls" --> UC["GameUseCase"]
    UC -- "Flow / Resource" --> VM
    VM -- "setState { copy(...) }" --> ST["MutableStateFlow&lt;S&gt;"]
    ST -- "collectAsStateWithLifecycle()" --> UI
```

The cycle runs in one direction only: the UI sends **intents** and renders **state**, and it never changes state itself.

## 4.2 Home

**Contract** (`HomeContract.kt`)

| State field | Type | Meaning |
|---|---|---|
| `pagingData` | `Flow<PagingData<Game>>` | Declared but unused. The UI collects `viewModel.gamesPagingData` instead |
| `search` | `String` | Current query |
| `genres` | `List<Genre>` | Chips to show |
| `selectedGenre` | `String` | Selected genre id (as a string) |

| Intent | Effect |
|---|---|
| `SearchGames(query)` | `_searchQuery.value = query` and update state |
| `SelectGenre(genreSlug)` | `_genreFilter.value = slug` and update state |
| `GetGenres` | Collect `getGenres()` and store the list on `Success`. Called from `init` |
| `Refresh()` | Reset search and genre to `""` |

**Reactive paging pipeline**

```mermaid
flowchart LR
    Q["_searchQuery<br/>MutableStateFlow"] -- "debounce(500ms)" --> C{{combine}}
    G["_genreFilter<br/>MutableStateFlow"] --> C
    C -- "(query, genre)" --> F["flatMapLatest"]
    F -- "getAllGame(query or null, genre or null)" --> P["Flow&lt;PagingData&lt;Game&gt;&gt;"]
    P -- "cachedIn(viewModelScope)" --> UI["collectAsLazyPagingItems()"]
```

**UI states derived from `LazyPagingItems.loadState`**

```mermaid
stateDiagram-v2
    [*] --> InitialLoading: refresh=Loading & itemCount=0
    InitialLoading --> ShowingList: refresh=NotLoading & items>0
    InitialLoading --> Empty: refresh=NotLoading & items=0
    InitialLoading --> ErrorScreen: refresh=Error & items=0
    ShowingList --> Refreshing: pull-to-refresh (refresh=Loading & items>0)
    Refreshing --> ShowingList
    ShowingList --> AppendLoading: scroll near end (append=Loading)
    AppendLoading --> ShowingList
    AppendLoading --> OfflineToast: append=Error(IOException)
    OfflineToast --> ShowingList

    InitialLoading: Shimmer LoadingCard ×20
    Empty: Lottie "not found"
    ErrorScreen: Lottie no_connection + "You Are Offline" / "Server Error"
    AppendLoading: LineScaleProgressIndicator at bottom
```

The search `TextField` lives in `MainActivity`'s `TopAppBar`, not in `HomeScreen`. That's why `MainActivity` creates `homeViewModel` with `koinViewModel()` and passes the same instance to `HomeScreen`.

## 4.3 Detail

**Contract** (`DetailContract.kt`)

| State | Intent |
|---|---|
| `isLoading: Boolean` | `GetDetail(id)`: collect `getDetailGame(id)` and map `Resource` to state |
| `game: Game?` | `ToggleFavorite(game)`: `setFavoriteGame(game, !isFavorite)`, then `copy(game = game.copy(isFavorite = new))` |
| `errorMessage: String?` | |

```mermaid
stateDiagram-v2
    [*] --> Loading: GetDetail(id)
    Loading --> Content: Resource.Success
    Loading --> Error: Resource.Error
    Error --> Loading: Retry (EmptyOrErrorState)
    Content --> Content: ToggleFavorite (FAB ♥ / ♡)

    Loading: LoadingCard shimmer (250dp picture, 50 rows)
    Content: GameDetailContent
    Error: EmptyOrErrorState + Retry
```

**Trailer player logic** (inside `GameDetailContent`)

```mermaid
flowchart TD
    A["retain { ExoPlayer }"] --> B{"game.movies<br/>not empty?"}
    B -- no --> Z[No trailer section]
    B -- yes --> C{"networkStatus == Available?"}
    C -- no --> PV["Show movies[0].preview image"]
    C -- yes --> D["setMediaItem(movies[index].url)<br/>prepare() + play()"]
    D --> E["Poll currentPosition every 16 ms while playing"]
    D --> F{"STATE_ENDED?"}
    F -- yes --> G["index = (index+1) % size<br/>load next, play if was playing"]
    G --> D
    H["Tap video"] --> I["Toggle PlayerUi visible"]
    I --> J{"visible & playing?"}
    J -- yes --> K["auto-hide after 3 s"]
    R["RetainedEffect onRetire"] --> S["removeListener + player.release()"]
```

Other Detail details:

- **Back handling**: a custom `OnBackPressedCallback` and the top-bar back icon both call `navigationManager.navigateUp()`.
- **Favorite FAB** is visible only while the vertical scroll is idle (`derivedStateOf { !isScrollInProgress }`).
- **Metascore colors**: 0–50 red, 51–74 yellow (`#F0C04F`), 75+ green (`#6DC849`).
- **Rating badge**: shown when `ratingTop` is 4 or 5. A 5 shows the `exceptional` drawable, and other values show `recommended`.
- **Platform mapping**: RAWG parent platform names map to drawables (pc, play_station, xbox, nintendo, linux, mac, ios, android, browser), de-duplicated, max 3 plus "+N".

## 4.4 Favorite (dynamic feature)

| State | Intent |
|---|---|
| `isLoading: Boolean` (never set to `true`) | `GetFavorites`: collect `getFavoriteGame()` and set `favorites` |
| `favorites: List<Game>` | |

- `FavoriteActivity.attachBaseContext` calls `SplitCompat.installActivity(this)` so it can access the split's code and resources.
- `onCreate` calls `loadKoinModules(favoriteModule)`, which registers `FavoriteViewModel` as a `factory`.
- Tapping an item calls `navigationManager.navigate(Screen.Detail(id))` and then `finish()`. `MainActivity`, which is back in the foreground, receives the command and opens Detail.

## 4.5 Shared UI components (`core/ui/components`)

| Component | Purpose |
|---|---|
| `GameItem` | Card with image, name, and meta info. Used in the Home grid and the Favorite list |
| `FilterChips` / `Chips` / `ChipText` | Genre filter row |
| `LoadingCard` + `Modifier.shimmerEffect` | Shimmer skeleton placeholders |
| `LineScaleProgressIndicator` | Custom animated loader |
| `LottieNotFoundAnimation` | Lottie empty/error illustration (`search_file.json`, `no_connection.json`) |
| `EmptyOrErrorState` | Error message with a retry button |
| `Label` | Pill chip for genres and tags |
| `StarRatingBar` | Star display for `rating` |
| `Height(n)` / `Width(n)` | Spacer helpers |

The theme is dark (`Background #151515`, `BackgroundCard #202020`, `StarGold #FFD700`), with edge-to-edge drawing and a light status bar style.
