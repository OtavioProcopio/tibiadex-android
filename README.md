# 🐉 Tibiadex - Tibia Encyclopedia for Android

<p align="center">
  <img src="https://tibiadata.com/images/tibiadata-logo-small.png" alt="TibiaData" width="200"/>
</p>

Aplicativo Android nativo que consome a API TibiaData v4 para exibir informações completas do MMORPG **Tibia**.

## 📱 Funcionalidades

- ✅ **Worlds** - Lista de mundos com detalhes e jogadores online
- ✅ **Characters** - Busca e visualização de personagens
- ✅ **Creatures** - Catálogo de criaturas com detalhes
- ✅ **Bosses** - Lista de bosses com boost atual
- ✅ **Spells** - Enciclopédia de magias com filtros
- ✅ **Highscores** - Rankings por categoria e vocação
- ✅ **Houses** - Sistema de casas com filtros por mundo/cidade
- ✅ **News** - Notícias do jogo com filtros avançados
- ✅ **Fansites** - Lista de fansites promovidos
- ⚡ **Kill Statistics** - Estatísticas de kills por mundo

## 🛠️ Tecnologias

- **Linguagem**: Java 11
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **Arquitetura**: MVC tradicional Android
- **Networking**: Volley 1.2.1 com VolleySingleton
- **Image Loading**: Picasso 2.71828
- **UI**: Material Design Components 1.10.0
- **Build**: Gradle 8.13.2 com Version Catalogs

## 📦 Estrutura do Projeto

```
app/src/main/java/com/byteunion/tibiadex/
├── data/
│   └── model/          # POJOs (World, Character, Creature, etc.)
├── network/
│   ├── ApiConstants.java      # URLs centralizadas
│   └── VolleySingleton.java   # RequestQueue singleton
├── ui/
│   ├── activity/      # 10 Activities principais
│   └── adapter/       # RecyclerView Adapters
└── util/
    └── DebounceHelper.java    # Helper para debounce em buscas
```

## 🚀 Como Executar

### Pré-requisitos

- Android Studio Ladybug | 2024.3.1 ou superior
- JDK 11
- Android SDK 36

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/SEU_USUARIO/tibiadex-android.git
cd tibiadex-android
```

2. Abra o projeto no Android Studio

3. Sincronize o Gradle:
```bash
./gradlew sync
```

4. Execute no emulador ou device:
```bash
./gradlew installDebug
```

## 🎨 Tema Visual

- **Cores Customizadas** (values/colors.xml):
  - `tibia_background`: #1B140F (marrom escuro)
  - `tibia_surface`: #2D2416 
  - `tibia_gold`: #D4AF37 (dourado Tibia)
  - `tibia_text_light`: #E8D7BF
- **Dark Theme** nativo com acentos dourados
- **Custom Spinners** para evitar contraste branco-no-branco

## 📡 API

Consome **TibiaData API v4**: https://api.tibiadata.com/v4/

### Endpoints Utilizados

| Endpoint | Uso |
|----------|-----|
| `/worlds` | Lista de mundos |
| `/world/{name}` | Detalhes + jogadores online |
| `/character/{name}` | Perfil de personagem |
| `/creatures` | Lista de criaturas |
| `/creature/{race}` | Detalhes da criatura |
| `/boostablebosses` | Bosses + boosted atual |
| `/spells` | Lista de magias |
| `/spell/{name}` | Detalhes da magia |
| `/highscores/{world}/{category}/{vocation}/{battleye}/{page}` | Rankings |
| `/houses/{world}/{town}` | Casas por cidade |
| `/house/{world}/{id}` | Detalhes da casa |
| `/news/archive/{days}` | Notícias (1/7/30/90 dias) |
| `/news/id/{id}` | Detalhes da notícia |
| `/killstatistics/{world}` | Kill stats |
| `/fansites` | Fansites promovidos |

## 🔄 Melhorias Implementadas

### Performance
- ✅ **VolleySingleton** - Reutilização de RequestQueue
- ✅ **Picasso** - Cache automático de imagens (memória + disco)
- ✅ **ApiConstants** - URLs centralizadas e reutilizáveis
- ✅ **Dual-List Filtering** - Preserva dados originais da API
- ✅ **Pagination** - Carregamento por demanda (PAGE_SIZE=20)
- ✅ **Click-to-Detail** - Dados pesados só ao clicar (Creatures, Houses, News)

### UX
- ✅ **Debounce Helper** - Evita lag ao digitar (300ms)
- ✅ **Visual Feedback** - Filtros ativos em dourado
- ✅ **Material Cards** - Hierarquia visual clara
- ✅ **Custom Spinners** - Consistência temática

## 📝 Padrões de Código

### Nomenclatura
- Activities: `{Feature}Activity.java`
- Layouts: `activity_{feature}.xml`, `item_{model}.xml`
- IDs: camelCase com prefixo (btnBack, tvTitle, recyclerPlayers)

### Models
POJOs com **campos públicos** (sem getters/setters):
```java
public class World {
    public String name;
    public String status;
    public int playersOnline;
}
```

### Network Pattern
```java
VolleySingleton.getInstance(this).addToRequestQueue(
    new JsonObjectRequest(Request.Method.GET, 
        ApiConstants.WORLDS, null,
        response -> { /* success */ },
        error -> { /* error toast */ }
    )
);
```

### Adapter Pattern
```java
Picasso.get()
    .load(imageUrl)
    .placeholder(R.color.tibia_surface)
    .error(R.color.tibia_surface)
    .into(imageView);
```

## 🧪 Testes (Futuro)

```bash
# Unit tests (JUnit)
./gradlew test

# Instrumented tests (Espresso)
./gradlew connectedAndroidTest
```

## 📦 Build Release

1. Gerar keystore:
```bash
keytool -genkey -v -keystore tibiadex-release.keystore \\
  -alias tibiadex -keyalg RSA -keysize 2048 -validity 10000
```

2. Configurar `app/build.gradle.kts` com signing config

3. Build:
```bash
./gradlew assembleRelease
```

APK em: `app/build/outputs/apk/release/app-release.apk`

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'feat: Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é apenas para fins educacionais. Tibia® é marca registrada da CipSoft GmbH.

## 🔗 Links

- [TibiaData API](https://tibiadata.com/)
- [Tibia Official](https://www.tibia.com/)
- [Material Design](https://m3.material.io/)
- [Picasso Documentation](https://square.github.io/picasso/)

---

**Desenvolvido com ☕ usando Java Android**
