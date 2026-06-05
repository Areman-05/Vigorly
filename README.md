# Vigorly

Entrena, mide tu actividad y sigue tu progreso con un flujo claro de inicio a sesión, historial y perfil atlético.

Vigorly es una app Android para quienes quieren un compañero de fitness sin ruido: ver el día en un panel con anillos de actividad, elegir entrenamientos del catálogo, completar sesiones guiadas y revisar historial, logros y análisis en pantallas separadas pero conectadas. No es un feed social ni un panel de métricas vacías: une onboarding, dashboard, entrenos activos, perfil identitario y análisis de rendimiento en un mismo viaje, con una interfaz moderna, legible y pensada para el uso diario.

## La idea en una frase

Llevar el pulso de tu cuerpo en un solo sitio: moverte hoy, entrenar con intención, entender tu evolución y celebrar constancia — sin mezclar apps de pasos, rutinas y estadísticas por separado.

## Para quién es

- Quien quiere **claridad al abrir la app**: saludo, anillos de actividad, meta diaria y recomendación del día en el inicio.
- Quien entrena con **rutinas reales**: catálogo filtrable, detalle con ejercicios y sesión activa con resumen al terminar.
- Quien valora **honestidad en los datos**: perfil atlético y estadísticas derivadas del historial; cuenta nueva sin números inventados.
- Quien separa **identidad y análisis**: Perfil para quién eres y qué has logrado; Análisis para cómo rindes esta semana.
- Quien cuida **sus datos locales**: cuentas, historial y preferencias en el dispositivo (DataStore); idioma configurable (es, en, ca, de, fr).

## Qué hace la app (en lenguaje humano)

### Inicio (Dashboard)

Saludo personalizado, anillos de actividad (mover, ejercicio, estar activo) y porcentaje de meta diaria. Debajo, métricas en mosaico: calorías de movimiento, pasos, minutos de ejercicio y horas activas. Incluye consejo del día, entrenamiento recomendado según tu perfil y progreso de la meta semanal. Un toque en los anillos abre el detalle de actividad con calendario para revisar otros días.

### Entrenamientos

Lista con búsqueda, filtros por tipo y ordenación. Cada rutina muestra imagen, duración estimada y nivel. El detalle explica objetivo, ejercicios y permite iniciar la sesión. Durante el entrenamiento activo avanzas por ejercicios; al completar, ves un resumen con duración, calorías y mensaje de cierre antes de volver al inicio.

### Historial

Cronología de sesiones completadas agrupada por fechas. Puedes abrir el detalle de cada entrada y filtrar por periodo. Es la fuente de verdad para rachas, nivel, logros y perfil atlético.

### Perfil

Tu identidad deportiva: avatar (presets o URL remota), nivel, racha, resumen de sesiones y perfil atlético en radar (fuerza, resistencia, movilidad, etc.) cuando hay entrenamientos reales. Escaparate de logros, accesos a Análisis y Logros completos, historial reciente y meta semanal. Sin historial, los estados vacíos explican qué aparecerá al entrenar.

### Análisis

Pantalla dedicada al rendimiento: media por sesión, minutos de la semana, mejor día, barras de actividad semanal y progreso de meta. Complementa al Perfil sin duplicar la identidad visual del usuario.

### Logros

Catálogo de hitos desbloqueables por constancia, volumen y variedad. Desde Perfil puedes equipar logros en el escaparate.

### Ajustes

Nombre visible, notificaciones, unidades, meta semanal con stepper, idioma de la app, reinicio del onboarding y cierre de sesión. Google Sign-In disponible si está configurado en el dispositivo; registro e inicio con email y contraseña funcionan sin backend externo.

### Onboarding

Tras registrarte, un asistente recoge objetivo fitness, nivel de actividad, sesiones semanales, ubicación preferida y horario. Esas preferencias alimentan recomendaciones y metas sin bloquear el acceso a la app principal.

### Actividad en segundo plano

Con permiso de reconocimiento de actividad, la app actualiza pasos y métricas del día; la UI refleja cambios al instante y persiste en disco de forma periódica para no penalizar fluidez.

## Por qué Vigorly y no “otra app de fitness”

- **Un solo viaje de usuario**: de “me registro” a “veo mi día”, “entreno”, “reviso historial” y “entiendo mi evolución” sin exportar datos a otra herramienta.
- **Perfil y análisis separados con sentido**: identidad y logros en Perfil; números y tendencias en Análisis. Misma base de datos, distinta pregunta respondida.
- **Datos honestos**: el perfil atlético y las estadísticas se calculan del historial; una cuenta nueva no muestra gráficos rellenos de mentira.
- **Experiencia cuidada**: anillos de actividad, tipografía propia, tarjetas con jerarquía clara, splash y navegación por pestañas (Inicio · Entrenos · Historial · Perfil).
- **Rendimiento como decisión de diseño**: fondo ligero en el shell principal, animaciones decorativas desactivadas en uso diario y persistencia de actividad espaciada para mantener scroll y transiciones fluidos.

## UI y UX: diseño, flujo y patrones

Esta sección recoge la intención detrás de la interfaz — uno de los aspectos que más se ha querido destacar en el proyecto.

### Principio rector: claridad antes que espectáculo

Las apps de fitness suelen competir por pantallas llenas de gradientes, animaciones infinitas y métricas que impresionan en capturas pero cansan al quinto uso. Vigorly apuesta por **legibilidad y ritmo**: el usuario debe saber en dos segundos dónde está, qué puede hacer y qué significa cada número. La estética inspira referencias como Apple Fitness o Nike Training en la jerarquía visual (anillos, stats grandes, secciones con aire), pero adaptada a una app local y sin depender de ecosistemas cerrados.

### Flujo de navegación

```text
Splash → (Login | Registro) → Setup → Shell principal (4 tabs)
                                              │
                    ┌─────────────────────────┼─────────────────────────┐
                    ▼                         ▼                         ▼
              Detalle actividad          Detalle entreno            Ajustes / Logros / Análisis
              (desde Dashboard)          → Sesión activa → Resumen   (desde top bar o Perfil)
```

- **Auth y onboarding** usan fondo con gradiente completo: marca la entrada y diferencia el “antes” del “después” de tener cuenta.
- **Shell principal** (tabs) usa un fondo más ligero y top bar contextual: menos GPU, más scroll cómodo.
- **Pantallas de detalle** comparten barra superior con retroceso y, cuando aplica, acceso a Ajustes — patrón predecible para no perder al usuario en rutas profundas.
- **Bottom bar** con cuatro destinos fijos y estado restaurado al cambiar de tab: el usuario puede alternar entre “¿cómo va mi día?” y “¿qué entreno?” sin perder contexto.

### Separación Perfil vs Análisis

Fue una decisión explícita de UX, no solo de carpetas:

| Pregunta del usuario | Dónde vive | Por qué |
|----------------------|------------|---------|
| ¿Quién soy como deportista? ¿Qué he desbloqueado? | **Perfil** | Identidad, avatar, nivel, escaparate, radar atlético |
| ¿Cómo voy esta semana? ¿Cuál fue mi mejor día? | **Análisis** | Métricas agregadas, barras semanales, progreso de meta |

Antes, insights y ajustes mezclaban accesos duplicados. Ahora cada pantalla tiene una sola responsabilidad narrativa: Perfil cuenta tu historia; Análisis responde “¿cómo voy?”.

### Patrones de interfaz

- **Anillos de actividad** como ancla visual del dashboard — referencia directa al lenguaje de los anillos de actividad diaria, pero con datos propios (movimiento, ejercicio, estar activo).
- **Tarjetas métricas** en grid 2×2: escaneo rápido sin tablas ni gráficos sobrecargados.
- **Estados vacíos con copy útil**: en lugar de ocultar secciones, se explica qué aparecerá al completar el primer entrenamiento.
- **Radar atlético** solo con datos reales: si no hay historial, no se dibuja un perfil falso — coherencia con la confianza del usuario.
- **Tipografía escalonada** (`DisplayStat`, `HeadlineLgMobile`, `LabelCaps`): números grandes para lo importante, etiquetas en mayúsculas para contexto — ritmo visual constante en Dashboard, Perfil y Análisis.
- **Test tags** en nodos clave: la UI está pensada también para pruebas E2E sin acoplar la experiencia humana a identificadores visibles.

### Rendimiento y percepción de fluidez

`UiPerformance` centraliza dos decisiones:

- `decorativeMotionEnabled = false` — sin bucles de animación en anillos, radar o fondos en el uso diario.
- `useLightMainBackground = true` — gradiente completo reservado para auth; tabs con fondo más simple.

La actividad del día se actualiza en memoria al momento y se escribe en DataStore cada pocos segundos (o al forzar en sesión/stop), para que el scroll no compita con I/O. En release, R8 y reglas ProGuard protegen modelos y ViewModels sin sacrificar depuración de crashes.

### Reflexión

El diseño de Vigorly no persigue “parecer pro” con efectos, sino **sentirse fiable**: números que significan algo, pantallas que no compiten entre sí y un camino corto desde abrir la app hasta terminar un entrenamiento. La interfaz es parte del producto — no un envoltorio del repositorio — y por eso comparte peso con la arquitectura y las pruebas en este repositorio.

## Cómo probarla en tu máquina

El núcleo funciona **sin backend propio**: cuentas, historial, preferencias y catálogo viven en DataStore en el dispositivo. Google Sign-In es opcional y requiere cuenta de Google en el emulador/dispositivo y clientes OAuth correctos si quieres probar ese flujo.

**Requisitos habituales:** Android Studio reciente, JDK 11+, SDK con API 26+ (`minSdk 26`).

```bash
# Windows (PowerShell o CMD)
gradlew.bat :app:assembleDebug

# macOS / Linux
./gradlew :app:assembleDebug
```

**No hay APK incluido en el repositorio** (la carpeta `build/` no se sube a Git). Generas el instalable en tu máquina con Gradle o desde Android Studio.

Tras compilar, el APK queda aquí:

| Build | Comando | Archivo |
|-------|---------|---------|
| Debug (pruebas) | `gradlew.bat :app:assembleDebug` | `app/build/outputs/apk/debug/app-debug.apk` |
| Release (optimizado) | `gradlew.bat :app:assembleRelease` | `app/build/outputs/apk/release/app-release-unsigned.apk` |

Copia el `.apk` al móvil e instálalo, o ejecuta directamente desde Android Studio en emulador o dispositivo. El release sale sin firmar: para distribuir fuera del IDE necesitas configurar tu keystore en `app/build.gradle.kts`.

## Tests automatizados

```bash
# Unitarios (JVM, sin emulador)
gradlew.bat :app:testDebugUnitTest

# Instrumentados (emulador o dispositivo conectado)
gradlew.bat :app:connectedDebugAndroidTest
```

Los **unitarios** cubren validación de auth, codecs de persistencia, calculadoras de actividad, rachas, nivel, perfil atlético, recomendador de entrenos, navegación (`NavigationUiState`) y más (~35 clases de test).

Los **instrumentados** (`VigorlyAppInstrumentedTest`) validan flujo de sesión, navegación por las cuatro tabs, ajustes, perfil con accesos a Análisis y Logros, persistencia de locale y arranque sin crash. Usan harness con semilla de cuenta, tags de UI y `clearPackageData` entre ejecuciones.

## Detalle técnico (opcional)

Si te interesa el cómo está hecha:

| Área | Tecnología |
|------|------------|
| UI | Kotlin, Jetpack Compose, Material 3 |
| Arquitectura | MVVM, `presentation/` por feature, `vigorlyNavGraph` |
| Datos | DataStore Preferences, codecs JSON para cuentas, historial y sesión |
| Actividad | Reconocimiento de actividad + tracker diario en memoria |
| Auth | Email/contraseña local, Google Sign-In (Credentials / Google ID) |
| Navegación | Navigation Compose, `NavigationUiState` derivado de ruta |
| DI | `VigorlyApplication` + `AppViewModelFactory` (instancia única de repositorio) |
| Build release | R8, shrink resources, reglas ProGuard específicas |
| Tests | JUnit, Robolectric, Compose UI Test, harness E2E en `androidTest` |

**Estructura de paquetes resumida:** `presentation/` (ViewModels y grafo de navegación), `data/` (repositorio, modelos, activity tracker, catálogo), `ui/` (pantallas y componentes Compose), `navigation/`, `util/`, `core/testing/`, `auth/`.

---

**Vigorly:** del primer paso al perfil atlético, con entrenos, historial y análisis en un solo sitio.
