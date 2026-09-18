# Vigorly

Entrena, mide tu actividad y sigue tu progreso con un flujo claro de inicio a sesión, historial y perfil.

Vigorly es una app Android para quienes quieren un compañero de fitness sin ruido: ver el día en un panel con anillos de actividad, elegir entrenamientos del catálogo, completar sesiones guiadas y revisar historial y análisis en pantallas separadas pero conectadas. No es un feed social ni un panel de métricas vacías: une onboarding, dashboard, entrenos activos, cuenta y análisis de rendimiento en un mismo viaje, con una interfaz moderna, legible y pensada para el uso diario.

## La idea en una frase

Llevar el pulso de tu cuerpo en un solo sitio: moverte hoy, entrenar con intención y entender tu evolución — sin mezclar apps de pasos, rutinas y estadísticas por separado.

## Para quién es

- Quien quiere **claridad al abrir la app**: anillos de actividad, meta del día y recomendación de entreno en el inicio.
- Quien entrena con **rutinas reales**: catálogo filtrable, listas propias, detalle con ejercicios y sesión guiada con resumen al terminar.
- Quien valora **honestidad en los datos**: estadísticas derivadas del historial; cuenta nueva sin números inventados.
- Quien separa **cuenta y análisis**: Perfil para quién eres y cómo está configurada la app; Análisis para peso, tendencias y periodo.
- Quien cuida **sus datos locales**: cuentas, historial y preferencias en el dispositivo (DataStore); idioma configurable (es, en, ca, de, fr).

## Qué hace la app (en lenguaje humano)

### Inicio (Dashboard)

Anillos de actividad (movimiento, ejercicio, pie) y porcentaje de meta diaria. Debajo, consejo del día, entrenamientos recomendados según tu perfil y progreso de la meta semanal. Un toque en los anillos abre el detalle de actividad con calendario para revisar otros días.

### Entrenar

Catálogo con búsqueda, filtros (zona, intensidad, duración, tipo) y listas propias. Cada rutina muestra imagen, duración y nivel. El detalle explica el objetivo y permite iniciar la sesión. Durante el entrenamiento avanzas por calentamiento, ejercicios y descansos; al completar, ves un resumen antes de volver.

### Análisis

Rendimiento y cuerpo: peso con objetivo y gráfico (el valor se arrastra los días sin registro), picker de periodo y una lectura de constancia. Las tarjetas de peso informan; no son atajos a otra pantalla. Complementa al Inicio y al Perfil sin copiarlos.

### Historial

No es una pestaña del menú inferior. Se abre desde Perfil. Calendario por día (como en Análisis), por defecto hoy: eliges una fecha y ves las sesiones de ese día. Es la fuente de verdad para rachas, nivel y logros.

### Perfil

Tu cuenta y los ajustes en un solo sitio: avatar, nombre, correo, idioma, unidades, avisos, preferencias de entreno, meta semanal, objetivo de peso, datos y cierre de sesión. Desde aquí entras al historial. Sin duplicar Análisis ni un cajón de logros aparte.

### Onboarding

Tras registrarte, el asistente explica el producto y recoge categorías del catálogo, nivel, dónde entrenas, horario, meta semanal y cómo funcionan los anillos. Esas preferencias alimentan recomendaciones, metas diarias y recordatorios.

### Actividad en segundo plano

Con permiso de reconocimiento de actividad, la app actualiza pasos y métricas del día; la UI refleja cambios al instante y persiste en disco de forma periódica para no penalizar fluidez.

## Por qué Vigorly y no “otra app de fitness”

- **Un solo viaje de usuario**: de “me registro” a “veo mi día”, “entreno”, “reviso el historial” y “entiendo el peso y el periodo” sin exportar datos a otra herramienta.
- **Cada pestaña una pregunta**: Inicio (qué hago hoy), Entrenar (con qué), Análisis (cómo voy), Perfil (quién soy y cómo está configurada la app). El historial responde “qué hice aquel día”, no “quién soy”.
- **Datos honestos**: nivel, racha y tendencias se calculan del historial; una cuenta nueva no muestra gráficos rellenos de mentira.
- **Experiencia cuidada**: anillos, tipografía propia (Bebas Neue + texto de sistema), cristal sobre aurora, splash con wordmark y navegación por cuatro tabs (Inicio · Entrenar · Análisis · Perfil).
- **Rendimiento como decisión de diseño**: fondo ligero en el shell principal, animaciones decorativas desactivadas en uso diario y persistencia de actividad espaciada para mantener scroll y transiciones fluidos.

## UI y UX: diseño, flujo y patrones

Esta sección recoge la intención detrás de la interfaz — uno de los aspectos que más se ha querido destacar en el proyecto.

### Principio rector: claridad antes que espectáculo

Las apps de fitness suelen competir por pantallas llenas de gradientes, animaciones infinitas y métricas que impresionan en capturas pero cansan al quinto uso. Vigorly apuesta por **legibilidad y ritmo**: el usuario debe saber en dos segundos dónde está, qué puede hacer y qué significa cada número. La estética inspira referencias como Apple Fitness o Nike Training en la jerarquía visual (anillos, stats grandes, secciones con aire), pero adaptada a una app local y sin depender de ecosistemas cerrados.

### Flujo de navegación

```text
Splash → (Login | Registro) → Setup → Shell principal (4 tabs)
         Inicio | Entrenar | Análisis | Perfil
              │         │         │         │
              ▼         ▼         ▼         ▼
     Detalle actividad  Detalle   Periodo   Historial (calendario)
     → métrica del día  entreno   y peso    → detalle de sesión
                        → Sesión → Resumen
```

- **Auth y onboarding** usan fondo aurora: marca la entrada y diferencia el “antes” del “después” de tener cuenta.
- **Shell principal** (tabs) usa un fondo más ligero y top bar contextual: menos GPU, más scroll cómodo.
- **Pantallas de detalle** comparten barra superior con retroceso — patrón predecible para no perder al usuario en rutas profundas.
- **Bottom bar** con cuatro destinos fijos y estado restaurado al cambiar de tab: el usuario puede alternar entre “¿cómo va mi día?” y “¿qué entreno?” sin perder contexto.

### Separación Perfil vs Análisis vs Historial

Fue una decisión explícita de UX, no solo de carpetas:

| Pregunta del usuario | Dónde vive | Por qué |
|----------------------|------------|---------|
| ¿Quién soy y cómo está configurada la app? | **Perfil** | Cuenta, avatar, idioma, prefs, meta semanal |
| ¿Cómo voy de peso y de periodo? | **Análisis** | Gráfico, objetivo, constancia; las cards no navegan |
| ¿Qué entrené aquel día? | **Historial** | Calendario diario, desde Perfil, no como quinta tab |

Antes, insights, ajustes y logros mezclaban accesos duplicados. Ahora cada superficie tiene una sola responsabilidad narrativa.

### Patrones de interfaz

- **Anillos de actividad** como ancla visual del dashboard — lenguaje de anillos diarios, con datos propios (movimiento, ejercicio, pie).
- **Wordmark** con la V en aurora fijo (gris y rosa); el resto del nombre en blanco.
- **Cristal sobre aurora**: tarjetas semitransparentes que dejan ver el fondo sin lavarlo.
- **Estados vacíos con copy útil**: en lugar de ocultar secciones, se explica qué aparecerá al completar el primer entrenamiento.
- **Tipografía escalonada** (`DisplayStat`, `HeadlineLgMobile`, `LabelCaps`): números grandes para lo importante, etiquetas en mayúsculas para contexto.
- **Test tags** en nodos clave: la UI está pensada también para pruebas E2E sin acoplar la experiencia humana a identificadores visibles.

### Rendimiento y percepción de fluidez

`UiPerformance` centraliza dos decisiones:

- `decorativeMotionEnabled = false` — sin bucles de animación en anillos o fondos en el uso diario.
- Fondo aurora completo reservado para auth; tabs con cierre a negro para no competir con el contenido.

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

Instala el APK en `app/build/outputs/apk/debug/` o ejecuta desde Android Studio en emulador o dispositivo.

Para build de release (minificación y shrink de recursos activos):

```bash
gradlew.bat :app:assembleRelease
```

## Tests automatizados

```bash
# Unitarios (JVM, sin emulador)
gradlew.bat :app:testDebugUnitTest

# Instrumentados (emulador o dispositivo conectado)
gradlew.bat :app:connectedDebugAndroidTest
```

Los **unitarios** cubren validación de auth, unicidad de cuenta, codecs de persistencia, calculadoras de actividad, rachas, nivel, recomendador, filtros, sesión, tips, navegación (`NavigationUiState`) y flujos del repositorio.

Los **instrumentados** (`VigorlyAppInstrumentedTest`) validan login hasta dashboard, las cuatro tabs, historial desde Perfil, stepper de meta semanal, persistencia de locale y arranque sin crash. Usan harness con semilla de cuenta, tags de UI y `clearPackageData` entre ejecuciones.

## Detalle técnico (opcional)

Si te interesa el cómo está hecha:

| Área | Tecnología |
|------|------------|
| UI | Kotlin, Jetpack Compose, Material 3 |
| Arquitectura | MVVM, `presentation/` por feature, `vigorlyNavGraph` |
| Datos | DataStore Preferences, codecs JSON para cuentas, historial, peso y playlists |
| Actividad | Reconocimiento de actividad + tracker diario en memoria |
| Auth | Email/contraseña local (unicidad de usuario y correo), Google Sign-In opcional |
| Navegación | Navigation Compose, `NavigationUiState` derivado de ruta |
| DI | `VigorlyApplication` + `AppViewModelFactory` (instancia única de repositorio) |
| Build release | R8, shrink resources, reglas ProGuard específicas |
| Tests | JUnit, Robolectric, Compose UI Test, harness E2E en `androidTest` |

**Estructura de paquetes resumida:** `presentation/` (ViewModels y grafo de navegación), `data/` (repositorio, modelos, activity tracker, catálogo), `ui/` (pantallas y componentes Compose), `navigation/`, `util/`, `core/testing/`, `auth/`.

**Versión actual de referencia:** 2.0 (`versionCode` 2).

---

**Vigorly:** del primer paso al cierre de sesión, con entrenos, historial y análisis en un solo sitio.
