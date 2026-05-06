# Mobile Shop

Mobile Shop is a Kotlin Jetpack Compose Android shopping app. It shows a small product catalog, lets users filter by category, add products to a cart, review cart totals, and complete a secure demo checkout using a simulated payment method.

The app is intentionally local-only: product data, cart quantities, and simulated payment state live in memory. It does not collect or store real card numbers, passwords, tokens, or personal payment details.

## Preview

GitHub will render these screenshots directly from the `docs/screenshots` folder.

| Product Catalog | Cart | Simulated Payment |
| --- | --- | --- |
| <img src="docs/screenshots/catalog.png" alt="Product catalog screen" width="240"> | <img src="docs/screenshots/cart.png" alt="Cart screen" width="240"> | <img src="docs/screenshots/payment.png" alt="Simulated payment screen" width="240"> |

## Features

- Kotlin + Jetpack Compose Android app.
- In-memory product catalog with six products and custom vector drawable artwork.
- Category filter chips for `All`, `Tech`, `Travel`, `Style`, `Home`, and `Study`.
- Quantity controls for adding and reducing cart items.
- Cart summary with live item count and total price.
- Mobile cart dialog opened from the bottom `Add to Cart` bar.
- Wide-screen layout with catalog and cart displayed side by side.
- Checkout flow with a simulated payment dialog.
- Demo payment options: `Demo Wallet` and `Demo Card`.
- Secure demo payment behavior: no real payment details are requested, saved, or transmitted.
- Android hardening: app backup disabled, cleartext traffic disabled, release minification enabled, and release resource shrinking enabled.
- Unit tests for cart totals, item counts, and currency formatting.

## Configure and Run

1. Install Android Studio with Android SDK Platform 36.
2. Use Java 21 for Gradle. This project pins Gradle to `/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home` in `gradle.properties`.
3. Open the project root folder, the folder containing `settings.gradle.kts`, in Android Studio.
4. Let Android Studio sync Gradle.
5. Start an Android emulator or connect an Android phone.
6. Press Run in Android Studio, or use the command line:

```bash
./gradlew test assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.mobileshop/.MainActivity
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

To create the hardened release build:

```bash
./gradlew assembleRelease
```

The release APK is generated at:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

## Project Structure

```text
MobileShop/
|-- app/
|   |-- build.gradle.kts
|   |-- proguard-rules.pro
|   `-- src/
|       |-- main/
|       |   |-- AndroidManifest.xml
|       |   |-- java/com/example/mobileshop/
|       |   |   |-- MainActivity.kt
|       |   |   `-- ShopModels.kt
|       |   `-- res/
|       |       |-- drawable/
|       |       |   |-- ic_store_bag.xml
|       |       |   |-- product_backpack.xml
|       |       |   |-- product_headphones.xml
|       |       |   |-- product_lamp.xml
|       |       |   |-- product_notebook.xml
|       |       |   |-- product_sneakers.xml
|       |       |   `-- product_watch.xml
|       |       `-- values/
|       |           |-- colors.xml
|       |           |-- strings.xml
|       |           `-- themes.xml
|       `-- test/java/com/example/mobileshop/
|           `-- ShopModelsTest.kt
|-- docs/screenshots/
|   |-- bag.png
|   |-- cart.png
|   |-- catalog.png
|   `-- payment.png
|-- gradle/wrapper/
|   |-- gradle-wrapper.jar
|   `-- gradle-wrapper.properties
|-- build.gradle.kts
|-- gradle.properties
|-- gradlew
|-- gradlew.bat
|-- README.md
|-- README.html
|-- README.pdf
`-- settings.gradle.kts
```

## App Flow

1. Browse products in the catalog.
2. Use category chips to filter visible products.
3. Tap `Add` on a product, then use `+` and `-` to adjust quantity.
4. On phones, tap the bottom `Add to Cart` button to open the cart.
5. Review items and the cart total.
6. Tap `Checkout`.
7. Choose `Demo Wallet` or `Demo Card`.
8. Tap `Pay` to complete the simulated payment and clear the cart.

## Security Notes

This project uses a simulated payment flow for coursework/demo purposes. It is safer than collecting fake card numbers because the UI never asks for sensitive card data.

Current hardening settings:

- `android:allowBackup="false"`
- `android:fullBackupContent="false"`
- `android:usesCleartextTraffic="false"`
- `isMinifyEnabled = true` for release builds
- `isShrinkResources = true` for release builds

There is no backend, authentication system, database, network API, or real payment processor in this app.

## Verification

The following commands were run successfully after the latest updates:

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Result: `BUILD SUCCESSFUL`.
