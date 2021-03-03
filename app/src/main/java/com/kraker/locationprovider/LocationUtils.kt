package com.kraker.locationprovider

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class LocationUtils {
    companion object {
        const val LOCATION_REQUEST_CODE = 123
        fun isGooglePlayServicesAvailable(activity: Activity?): Boolean {
            val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
            val status: Int = googleApiAvailability.isGooglePlayServicesAvailable(activity)
            if (status != ConnectionResult.SUCCESS) {
                return false
            }
            return true
        }
    }
}