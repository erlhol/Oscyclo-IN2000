package com.example.sykkelapp.ui.map.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

// https://github.com/mayowa-egbewunmi/LocationUpdateWithLiveData
class LocationLiveData (context: Context) : LiveData<LocationModel>() {

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private fun setLocationData(location : Location) {
        value = LocationModel(
            longitude = location.longitude,
            latitude = location.latitude
        )
    }

    companion object {
        val locationRequest : LocationRequest = create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0.locations.forEach {
                setLocationData(it)

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                location : Location? ->
                location?.also {
                    setLocationData(it)
                }
            }
        startLocationUpdates()
    }
}