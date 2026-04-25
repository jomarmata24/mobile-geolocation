<?php

namespace Projectmata\MobileGeolocation\Facades;

use Illuminate\Support\Facades\Facade;

/**
 * @method static array getCurrentPosition(bool $highAccuracy = true)
 * @method static array requestPermission()
 */
class Geolocation extends Facade
{
    protected static function getFacadeAccessor(): string
    {
        return 'projectmata.mobile-geolocation';
    }
}