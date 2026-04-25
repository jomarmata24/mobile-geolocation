# Projectmata Mobile Geolocation

[![Latest Version](https://img.shields.io/packagist/v/projectmata/mobile-geolocation.svg)](https://packagist.org/packages/projectmata/mobile-geolocation)
[![Total Downloads](https://img.shields.io/packagist/dt/projectmata/mobile-geolocation.svg)](https://packagist.org/packages/projectmata/mobile-geolocation)
[![License](https://img.shields.io/packagist/l/projectmata/mobile-geolocation.svg)](https://packagist.org/packages/projectmata/mobile-geolocation)

Geolocation plugin for [NativePHP Mobile](https://nativephp.com). Reads the device's current position on Android and iOS with a single call.

## Requirements

- PHP `^8.1`
- Laravel `^11.0` or `^12.0` / `^13.0`
- `nativephp/mobile`
- Android: `min_version 33`, depends on `com.google.android.gms:play-services-location:21.3.0`
- iOS: `min_version 18.2`

## Installation

```bash
composer require projectmata/mobile-geolocation
```

Laravel auto-discovery registers the service provider and facade automatically.

Rebuild the mobile app so NativePHP wires up the native plugin and permissions:

```bash
php artisan native:run android
# or
php artisan native:run ios
```

## Permissions

The package declares these for you via its `nativephp.json` manifest — you don't need to add them manually:

- **Android** — `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`
- **iOS** — `NSLocationWhenInUseUsageDescription` is set to *"This app uses your location to show your current position."* Override it in your app's `info_plist` config if you want different copy.

Always call `requestPermission()` before requesting a position, and handle the case where the user denies.

## Usage

### PHP (Laravel)

```php
use Projectmata\MobileGeolocation\Facades\Geolocation;

// Ask the OS for location permission (no-op if already granted)
$perm = Geolocation::requestPermission();

// Then read the position
$pos = Geolocation::getCurrentPosition(highAccuracy: true);

// $pos = ['success' => true, 'latitude' => ..., 'longitude' => ..., 'accuracy' => ...]
```

### JavaScript (in-app)

The plugin registers itself on `window.NativePHP.Geolocation`:

```js
await window.NativePHP.Geolocation.RequestPermission();

const pos = await window.NativePHP.Geolocation.GetCurrentPosition({ highAccuracy: true });

if (pos.success) {
    console.log(pos.latitude, pos.longitude, pos.accuracy);
}
```

Or as a bundled import:

```js
import Geolocation, { getCurrentPosition } from 'projectmata-mobile-geolocation';
```

## Bridge methods

| Method                            | Params                       | Returns                                                    |
| --------------------------------- | ---------------------------- | ---------------------------------------------------------- |
| `Geolocation.RequestPermission`   | —                            | `{ success, granted }`                                     |
| `Geolocation.GetCurrentPosition`  | `{ highAccuracy: boolean }`  | `{ success, latitude, longitude, accuracy, timestamp }`    |

The `highAccuracy` flag maps to Fused Location's high-accuracy mode on Android and `kCLLocationAccuracyBest` on iOS.

## License

MIT
