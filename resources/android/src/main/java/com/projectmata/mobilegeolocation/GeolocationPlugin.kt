package com.projectmata.mobilegeolocation

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.nativephp.mobile.bridge.BridgeError
import com.nativephp.mobile.bridge.BridgeFunction
import com.nativephp.mobile.bridge.BridgeResponse

object GeolocationPlugin {

    private fun makeError(code: String, message: String): BridgeError {
        val ctor = BridgeError::class.java.getDeclaredConstructor(
            String::class.java,
            String::class.java
        )
        ctor.isAccessible = true
        return ctor.newInstance(code, message)
    }

    class RequestPermission(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val fineGranted = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            return if (fineGranted || coarseGranted) {
                BridgeResponse.success(
                    mapOf(
                        "granted" to true,
                        "message" to "Permission already granted"
                    )
                )
            } else {
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )

                BridgeResponse.success(
                    mapOf(
                        "granted" to false,
                        "message" to "Permission requested"
                    )
                )
            }
        }
    }

    class GetCurrentPosition(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val fineGranted = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!fineGranted && !coarseGranted) {
                return BridgeResponse.error(
                    makeError("LOCATION_PERMISSION_DENIED", "Location permission not granted")
                )
            }

            return try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                val task = fusedLocationClient.lastLocation
                val location = Tasks.await(task)

                if (location == null) {
                    BridgeResponse.error(
                        makeError("LOCATION_UNAVAILABLE", "Location unavailable")
                    )
                } else {
                    BridgeResponse.success(
                        mapOf(
                            "success" to true,
                            "latitude" to location.latitude,
                            "longitude" to location.longitude,
                            "accuracy" to location.accuracy.toDouble(),
                            "timestamp" to location.time
                        )
                    )
                }
            } catch (e: Exception) {
                BridgeResponse.error(
                    makeError("LOCATION_ERROR", e.message ?: "Unknown location error")
                )
            }
        }
    }
}