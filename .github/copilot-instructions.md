# Tibiadex Android App - AI Coding Guidelines

## Project Overview
Tibiadex is an Android app that consumes the TibiaData API (v4) to display game information from the MMORPG Tibia. The app uses vanilla Android (Java 11) with Material Design components.

## Architecture

### Structure
- **Package**: `com.byteunion.tibiadex`
- **Pattern**: Traditional Android MVC with activity-centric architecture
- **Navigation**: Intent-based with `MainActivity` as hub
- **Data Layer**: Simple POJOs in `data/model/` (no Room, no ViewModel)
- **Network**: Volley library for all HTTP requests (synchronous request queue pattern)

### Key Components
- **Activities** (`ui/activity/`): Each feature is a separate activity (8 total including MainActivity)
- **Adapters** (`ui/adapter/`): RecyclerView adapters with ViewHolder pattern and click listeners via interfaces
- **Models** (`data/model/`): Plain public-field POJOs (Boss, World, OnlinePlayer, KillStatistic, CharacterProfile)

## Critical Patterns

### API Integration
- **Base URL**: `https://api.tibiadata.com/v4/`
- **Pattern**: URL strings are inline in activities (no central API service class)
- **Common endpoints**:
  - `/worlds` - list all worlds
  - `/world/{name}` - world details + online players
  - `/killstatistics/{world}` - creature kill stats
  - `/character/{name}` - character profile (URL-encoded)
  - `/boostablebosses` - boss list + boosted boss

### Network Request Pattern (Volley)
```java
String url = "https://api.tibiadata.com/v4/endpoint";
Volley.newRequestQueue(this).add(
    new JsonObjectRequest(Request.Method.GET, url, null,
        response -> {
            // Success: parse JSON and update UI
            JSONObject data = response.getJSONObject("wrapper_key");
            // ... parse and populate lists
            adapter.notifyDataSetChanged();
        },
        error -> Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show()
    )
);
```

### RecyclerView Adapter Pattern
```java
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> {
    public interface OnItemClick { void onClick(Item item); }
    
    private final List<Item> items;
    private final OnItemClick listener;
    
    // Constructor, onCreateViewHolder, onBindViewHolder with listener.onClick()
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView field1, field2;
        ViewHolder(View v) { super(v); /* findViewById bindings */ }
    }
}
```

### Data Model Convention
All models use **public fields** (no getters/setters). Example:
```java
public class World {
    public String name;
    public String status;
    public int playersOnline;
    // Constructor with all fields
}
```

## UI/UX Patterns

### Theming
- **Custom color scheme** with `tibia_` prefix in [colors.xml](app/src/main/res/values/colors.xml)
- Colors: `tibia_background`, `tibia_surface`, `tibia_gold`, `tibia_gold_light`, `tibia_text_light`, `tibia_text_dark`
- Dark theme with gold accents matching Tibia game aesthetic

### Activity Navigation
- Back button pattern: `Button btnBack = findViewById(R.id.btnBack); btnBack.setOnClickListener(v -> finish());`
- Intent extras for passing data: `i.putExtra("world_name", world.name);`

### Collapsible Sections
[WorldDetailActivity](app/src/main/java/com/byteunion/tibiadex/ui/activity/WorldDetailActivity.java) implements tab-like behavior:
- Toggle buttons with Unicode arrows (▼/▲) and emojis (☠)
- State tracking via enum `Tab { NONE, PLAYERS, KILLS }`
- Visibility toggling with `setVisibility(View.GONE)` / `VISIBLE`

### List Filtering
Two-tier filtering pattern (see [WorldDetailActivity](app/src/main/java/com/byteunion/tibiadex/ui/activity/WorldDetailActivity.java)):
1. **Search by name**: TextWatcher on search field filtering `allItems` → `filteredItems`
2. **Filter buttons**: Horizontal scroll view with vocation/type filters
3. Always call `adapter.notifyDataSetChanged()` after modifying filtered list

### Pagination
[BossesActivity](app/src/main/java/com/byteunion/tibiadex/ui/activity/BossesActivity.java) shows infinite scroll pattern:
- Load data in `PAGE_SIZE` chunks from `allBosses` → `visibleBosses`
- OnScrollListener detecting bottom with `isLoading` flag to prevent duplicate loads

## Build System

### Gradle Setup
- **Version catalogs**: Dependencies in [gradle/libs.versions.toml](gradle/libs.versions.toml)
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **Java**: 11 (sourceCompatibility/targetCompatibility)
- **Key dependencies**: Volley 1.2.1, RecyclerView 1.3.2/1.4.0, Material 1.10.0

### Build Commands
```bash
./gradlew assembleDebug      # Build debug APK
./gradlew installDebug       # Install on connected device
./gradlew clean             # Clean build artifacts
```

## Development Workflow

### Adding New Features
1. Create activity in `ui/activity/` with corresponding layout XML
2. Add activity to [AndroidManifest.xml](app/src/main/AndroidManifest.xml) with `android:exported="false"`
3. Create model POJO in `data/model/` if needed
4. Create adapter in `ui/adapter/` if displaying list
5. Add navigation from MainActivity or other activity via Intent

### Common Tasks
- **New API endpoint**: Add URL string in activity, create Volley request with JSON parsing
- **New list view**: Create model, adapter with ViewHolder, item layout XML, wire to activity
- **Search/filter**: Implement `allItems` + `filteredItems` pattern with TextWatcher

## Conventions

### Naming
- Activities: `{Feature}Activity.java` (e.g., WorldsActivity, BossesActivity)
- Layouts: `activity_{feature}.xml`, `item_{model}.xml`
- IDs: camelCase with prefix (btnBack, tvTitle, recyclerPlayers, etSearch)
- Colors: `tibia_` prefix for theme colors

### Portuguese UI
User-facing strings are in **Portuguese**: "Jogadores", "Erro de conexão", "Buscar criatura..."

### Error Handling
Standard pattern: Toast message on API error
```java
error -> Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show()
```

### Permissions
- **INTERNET** permission required in [AndroidManifest.xml](app/src/main/AndroidManifest.xml) for API calls

## Known Limitations
- No offline mode or local database (Room/SQLite)
- No dependency injection (Hilt/Dagger)
- No reactive streams (RxJava/Coroutines)
- No ViewModel/LiveData architecture components
- No image caching library (using Volley ImageRequest)
