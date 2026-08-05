# Control de Abonos — App Android nativa para Tablet

App nativa en **Kotlin + Jetpack Compose**, con base de datos **local (Room/SQLite)**,
optimizada para pantallas de tablet, para el control de abonos y pedidos de clientes en **COP**.

## Estructura del proyecto

```
ControlAbonos/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/abonos/control/
│       │   ├── MainActivity.kt
│       │   ├── ControlAbonosApp.kt
│       │   ├── data/            (Entities, DAOs, AppDatabase, Repositorio)
│       │   ├── ui/theme/        (Color, Type, Theme — paleta Azul/Dorado/Blanco)
│       │   ├── ui/navigation/   (NavGraph)
│       │   ├── ui/screens/      (Login, Clientes, ClienteDetalle, AgregarCliente, Usuarios, Reportes)
│       │   ├── ui/components/   (TarjetaCliente)
│       │   ├── viewmodel/       (LoginVM, ClientesVM, ClienteDetalleVM, UsuariosVM, ReportesVM)
│       │   └── util/            (FormatoCop, ExportadorPdf, ExportadorExcel, UtilCompartir)
│       └── res/
├── build.gradle.kts
└── settings.gradle.kts
```

### Notas de diseño importantes
- **Usuario administrador por defecto**: la primera vez que corre la app se crea automáticamente
  el usuario `admin` / `admin123` (ver `AppDatabase.kt`). **Cámbialo** apenas entres, desde el
  módulo de Usuarios.
- **Regla de administrador único**: `Repositorio.eliminarUsuario()` bloquea el borrado del último
  administrador, cumpliendo la regla de que la base nunca puede quedar sin uno.
- **Formato COP**: `FormatoCop.kt` centraliza el formato `$150.000` en toda la app.
- **Grid adaptable**: `ClientesScreen` usa `GridCells.Adaptive`, así que en tablets grandes se ven
  más columnas automáticamente sin tocar código.
- **PDF**: se genera con la API nativa `android.graphics.pdf.PdfDocument` (sin librerías externas).
- **Excel (.xlsx)**: se genera con la librería liviana `FastExcel` (más estable en Android que Apache POI).
- **Compartir**: `UtilCompartir.kt` usa `Intent.ACTION_SEND` con `FileProvider`, e intenta abrir
  WhatsApp o WhatsApp Business directamente; si no están instalados, cae al selector de Android.

---

## 1. Configurar el entorno de desarrollo

1. **Instala Android Studio** (versión "Koala" o más reciente):
   https://developer.android.com/studio
2. Al abrir Android Studio por primera vez, deja que el asistente descargue:
   - Android SDK (API 34 — Android 14)
   - Android SDK Build-Tools
   - Android Emulator (opcional, si quieres probar sin tablet física)
3. Verifica que tengas **JDK 17** (Android Studio moderno lo trae incluido — "JBR" — no necesitas instalarlo aparte).
4. Abre Android Studio → **File → Open** → selecciona la carpeta `ControlAbonos` (la raíz del proyecto, donde está `settings.gradle.kts`).
5. Espera a que termine el **Gradle Sync** (barra inferior). La primera vez puede tardar varios minutos porque descarga las dependencias (Compose, Room, Navigation, FastExcel).
6. Si Gradle Sync falla por versiones, ve a **File → Project Structure → SDK Location** y confirma que el SDK esté bien apuntado.

---

## 2. Compilar la app y generar el APK instalable

### Opción A — Generar el APK desde el menú (recomendado)
1. En Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. Espera a que compile. Al terminar aparece una notificación abajo a la derecha: **"locate"**.
3. Haz clic en **"locate"** (o navega manualmente) para encontrar el archivo generado en:
   ```
   ControlAbonos/app/build/outputs/apk/debug/app-debug.apk
   ```
   Este es el instalable que se copia a la tablet.

### Opción B — Generar el APK por línea de comandos
Desde una terminal, dentro de la carpeta del proyecto:

```bash
# En Windows
gradlew.bat assembleDebug

# En macOS/Linux
./gradlew assembleDebug
```

El APK queda en la misma ruta: `app/build/outputs/apk/debug/app-debug.apk`.

### Sobre el APK "release" (opcional, para distribución final)
El **debug APK** es perfecto para instalar y probar en tu propia tablet. Si más adelante quieres
compartir la app de forma más "oficial" (firmada), en Android Studio ve a
**Build → Generate Signed Bundle / APK**, crea un *keystore* (guarda ese archivo y su contraseña
en un lugar seguro, lo necesitarás para cualquier actualización futura) y genera un **APK release**.

---

## 3. Pasar el APK a la Tablet, instalarlo y ejecutarlo

### Paso 1 — Habilitar "Orígenes desconocidos" en la tablet
Como el APK no viene de Google Play, Android bloquea la instalación por defecto:
1. En la tablet, ve a **Ajustes → Seguridad** (o **Ajustes → Apps → Acceso especial**).
2. Busca **"Instalar apps desconocidas"**.
3. Selecciona la app que usarás para abrir el archivo (por ejemplo, tu explorador de archivos o Gmail) y activa **"Permitir desde esta fuente"**.

### Paso 2 — Copiar el archivo APK a la tablet
Cualquiera de estas opciones funciona:

- **Cable USB**: conecta la tablet a tu PC, autoriza la transferencia de archivos (MTP) en la
  tablet, y copia `app-debug.apk` a la carpeta *Descargas* de la tablet.
- **Correo electrónico**: envíate el APK como adjunto y ábrelo desde el correo en la tablet.
- **Google Drive / WhatsApp Web**: sube el APK a Drive (o envíatelo por WhatsApp) y descárgalo
  directamente desde la tablet.
- **ADB (avanzado)**: con la tablet en modo depuración USB y conectada al PC:
  ```bash
  adb install app/build/outputs/apk/debug/app-debug.apk
  ```
  Esto instala la app directamente sin pasos manuales adicionales.

### Paso 3 — Instalar
1. En la tablet, abre el **explorador de archivos** (o la app de Descargas) y toca `app-debug.apk`.
2. Aparecerá una pantalla de confirmación de Android mostrando los permisos de la app → toca **"Instalar"**.
3. Espera unos segundos y toca **"Abrir"**.

### Paso 4 — Primer uso
1. La app abre en la pantalla de **Login**.
2. Ingresa con el usuario administrador por defecto:
   - Usuario: `admin`
   - Contraseña: `admin123`
3. Ve al módulo **Usuarios** y cambia esa contraseña (o crea tu propio administrador y luego elimina el de prueba — el sistema no te dejará quedarte sin ninguno).
4. Empieza a registrar clientes desde el botón **"Agregar cliente"** en la pantalla principal.

Como la base de datos es local (Room/SQLite dentro del dispositivo), la app funciona **sin
necesidad de internet** en todo momento; solo se usaría conexión si en algún punto decides
compartir un reporte por correo o WhatsApp.
