
# Gestión de oportunidades

App móvil Android (Jetpack Compose) para registrar y dar seguimiento a oportunidades comerciales: eventos, ferias y jornadas de beneficios.

Proyecto de DSY1105 – Desarrollo de Aplicaciones Móviles, Evaluación Parcial 2.

## Integrantes

- Martin Jaque
- Liza Micele

## Funcionalidades

- Inicio de sesión con cuenta ficticia
- Menú principal
- Lista de oportunidades: crear, editar y eliminar
- Formulario con validación por campo y mensajes de error
- Persistencia local con Room (los datos siguen al cerrar la app)
- Cámara: foto de evidencia de la oportunidad
- Ubicación: coordenadas del lugar de la oportunidad
- Animaciones: transiciones entre pantallas, color de prioridad y mensajes animados

## Arquitectura

MVVM: pantallas (`ui`) → ViewModel → Repository → DAO y Room (`data`).

## Cómo ejecutar

1. Clonar el repositorio y abrirlo con Android Studio.
2. Esperar a que termine la sincronización de Gradle.
3. Ejecutar la app en un emulador o en un teléfono Android.
4. Iniciar sesión con usuario `admin` y contraseña `123`.
5. Para probar la ubicación en el emulador: Extended controls (…) → Location → Set location.

## Planificación

- Trello: https://trello.com/invite/b/6aa9b125e035f77e79c4f9ad/ATTI4ceaa2ef355991dccf825eee48be43920FFE88AF/evparcial2
- Repositorio:https://github.com/M4rtinj4ke1/Evaluacion2