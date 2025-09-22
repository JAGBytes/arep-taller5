# Frontend - Sistema de Gestión de Propiedades

## Descripción

Frontend desarrollado en HTML, CSS y JavaScript para el sistema de gestión de propiedades. Se conecta con el backend REST API desarrollado en Spring Boot.

## Características

### ✅ Interfaz de Usuario

- **Diseño moderno y responsive** - Se adapta a diferentes tamaños de pantalla
- **Interfaz intuitiva** - Fácil navegación y uso
- **Tema profesional** - Colores y tipografía modernos

### ✅ Gestión de Propiedades

- **Lista de propiedades** - Visualización en tarjetas con información clave
- **Formulario de creación** - Para agregar nuevas propiedades
- **Formulario de edición** - Para modificar propiedades existentes
- **Vista detallada** - Modal con información completa de la propiedad
- **Eliminación** - Con confirmación para evitar eliminaciones accidentales

### ✅ Validación del Cliente

- **Campos obligatorios** - Dirección, precio y tamaño son requeridos
- **Validación de tipos** - Precio y tamaño deben ser números positivos
- **Validación de longitud** - Dirección mínima 5 caracteres, descripción máxima 1000
- **Mensajes de error** - Feedback visual inmediato al usuario

### ✅ Comunicación con Backend

- **API REST** - Comunicación completa con todos los endpoints
- **Manejo de errores** - Gestión de errores de red y servidor
- **Notificaciones** - Toast messages para feedback al usuario
- **Estados de carga** - Indicadores visuales durante las operaciones

## Archivos del Frontend

### `index.html`

- Estructura principal de la aplicación
- Formularios para crear/editar propiedades
- Lista de propiedades con acciones
- Modal para vista detallada

### `styles.css`

- Estilos modernos y responsive
- Tema profesional con gradientes
- Animaciones y transiciones suaves
- Diseño mobile-first

### `script.js`

- Lógica de la aplicación
- Comunicación con API REST
- Validación del lado del cliente
- Manejo de eventos y UI

## Uso

### 1. Iniciar el Backend

```bash
# En el directorio del proyecto
mvn spring-boot:run
```

El backend estará disponible en `http://localhost:8080`

### 2. Abrir el Frontend

Simplemente abre `index.html` en tu navegador web. El frontend se conectará automáticamente al backend.

### 3. Funcionalidades Disponibles

#### Ver Propiedades

- La lista se carga automáticamente al abrir la aplicación
- Cada propiedad se muestra en una tarjeta con información resumida

#### Agregar Nueva Propiedad

1. Haz clic en "Nueva Propiedad"
2. Completa el formulario (campos marcados con \* son obligatorios)
3. Haz clic en "Guardar"

#### Editar Propiedad

1. Haz clic en "Editar" en la tarjeta de la propiedad
2. Modifica los campos necesarios
3. Haz clic en "Guardar"

#### Ver Detalles

1. Haz clic en "Ver" o en la tarjeta de la propiedad
2. Se abrirá un modal con información detallada

#### Eliminar Propiedad

1. Haz clic en "Eliminar" en la tarjeta de la propiedad
2. Confirma la eliminación en el diálogo

## API Endpoints Utilizados

- `GET /api/properties` - Obtener todas las propiedades
- `GET /api/properties/{id}` - Obtener propiedad por ID
- `POST /api/properties` - Crear nueva propiedad
- `PUT /api/properties/{id}` - Actualizar propiedad
- `DELETE /api/properties/{id}` - Eliminar propiedad

## Validaciones Implementadas

### Campos Obligatorios

- **Dirección**: Mínimo 5 caracteres
- **Precio**: Número mayor a 0
- **Tamaño**: Número mayor a 0

### Campos Opcionales

- **Descripción**: Máximo 1000 caracteres

## Características Técnicas

### Responsive Design

- Diseño mobile-first
- Breakpoints para tablet y desktop
- Grid layout adaptativo

### Accesibilidad

- Navegación por teclado
- Contraste adecuado
- Iconos descriptivos

### Performance

- Lazy loading de propiedades
- Debouncing en validaciones
- Optimización de re-renders

## Navegadores Soportados

- Chrome 80+
- Firefox 75+
- Safari 13+
- Edge 80+

## Notas de Desarrollo

- El frontend asume que el backend está corriendo en `http://localhost:8080`
- Si el backend corre en otro puerto, modifica `API_BASE_URL` en `script.js`
- Las validaciones del cliente son complementarias a las del servidor
- El diseño es completamente responsive y funciona en dispositivos móviles
