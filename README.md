# Gestión de oportunidades (Evaluacion2)

App móvil Android (Jetpack Compose) para registrar y dar seguimiento a oportunidades comerciales:
eventos, ferias, jornadas de beneficios y otras instancias donde se puede ofrecer un servicio.
Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Evaluación Parcial 2).

## Integrantes

- Martin Jaque
- Liza Micele

## Funcionalidades

- **Login** con cuenta ficticia (validación de campos vacíos y de credenciales, íconos y mensajes de error).
- **Menú principal** con acceso a la lista y cierre de sesión.
- **Lista de oportunidades** con foto, tipo, prioridad (indicador de color), estado y ubicación.
- **Formulario** para crear y editar oportunidades, con validación por campo y mensajes visuales.
- **Persistencia local** con Room (SQLite): los datos siguen ahí al cerrar la app.
- **Recurso nativo 1 – Cámara:** foto de evidencia de la oportunidad (con solicitud de permiso).
- **Recurso nativo 2 – Ubicación:** coordenadas del lugar de la oportunidad (con solicitud de permiso).
- **Animaciones:** transiciones entre pantallas, color de prioridad animado, aparición animada de la
  ubicación y de los mensajes, y fundido en el botón de guardar.

## Arquitectura (MVVM)

```
com.example.evaluacion2
├── MainActivity.kt
├── data
│   ├── local         OportunidadEntity, OportunidadDao, AppDatabase (Room)
│   └── repository    OportunidadRepository, AuthRepository
└── ui
    ├── components    SeccionFoto, ImagenInteligente, SeccionUbicacion
    ├── login         LoginUiState, LoginViewModel, LoginScreen
    ├── menu          MenuScreen
    ├── navegacion    AppNavigation
    ├── oportunidad   OportunidadUiState, OportunidadViewModel, OportunidadOpciones,
    │                 ListaOportunidadesScreen, OportunidadFormScreen
    └── theme         Color, Type, Theme
```

Las pantallas solo dibujan y avisan los eventos del usuario. Las validaciones y las decisiones viven
en los ViewModel, y el acceso a datos pasa siempre por el Repository.

## Cómo ejecutar

1. Clonar el repositorio y abrirlo con **Android Studio** (versión reciente).
2. Esperar la sincronización de Gradle (*Sync Now*).
3. Ejecutar en un emulador o en un teléfono con Android 7.0 (API 24) o superior.
4. Iniciar sesión con la cuenta de prueba: usuario `admin` y contraseña `123`.
5. En el emulador, para probar la ubicación: *Extended controls (…) → Location → Set location*.

Si se actualiza desde una versión anterior de la base de datos, la migración 1→2 conserva los datos.

## Planificación y trabajo en equipo

- Tablero Trello: _pegar enlace_
- Repositorio: _pegar enlace_
