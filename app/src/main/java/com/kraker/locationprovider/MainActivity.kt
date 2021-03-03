package com.kraker.locationprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.kraker.locationprovider.LocationUtils.Companion.LOCATION_REQUEST_CODE
import com.kraker.locationprovider.LocationUtils.Companion.isGooglePlayServicesAvailable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // Listener used for the LocationManager request
    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            displayLocation(location, "GPS")
        }

        // Not used for the moment
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderDisabled(provider: String?) {}
        override fun onProviderEnabled(provider: String?) {}
    }

    // Listener used for the FusedLocation request
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                displayLocation(location, "fused")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Display if the Google Play Services are available or not
        servicesStatusTextview.text = if (isGooglePlayServicesAvailable(this)) {
            getString(R.string.google_play_services_installed)
        } else {
            getString(R.string.google_play_services_not_installed)
        }

        // Start the request location process
        checkPermissionAndRequestLocation()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        }
    }

    private fun checkPermissionAndRequestLocation() {
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_REQUEST_CODE
            )
            return
        } else {
            requestLocation()
        }
    }

    private fun requestLocation() {
        if (isGooglePlayServicesAvailable(this)) {
            requestLocationUsingFusedLocationProvider()
        } else {
            requestLocationUsingLocationManager()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUsingLocationManager() {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000L,
                    10F,
                    mLocationListener
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUsingFusedLocationProvider() {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000L,
                    10F,
                    mLocationListener
            )
        }
    }

    private fun displayLocation(location: Location?, source: String) {
        locationSourceTextView.text = getString(R.string.location_source_placeholder, source)
        longitudeTextView.text = getString(R.string.location_longitude_placeholder, location?.longitude)
        latitudeTextView.text = getString(R.string.location_latitude_placeholder, location?.latitude)
    }
}