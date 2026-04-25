<?php

namespace Projectmata\MobileGeolocation;

use Illuminate\Support\ServiceProvider;

class MobileGeolocationServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton('projectmata.mobile-geolocation', function () {
            return new GeolocationManager();
        });
    }

    public function boot(): void
    {
        //
    }
}