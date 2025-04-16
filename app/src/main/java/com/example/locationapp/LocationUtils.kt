package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(val context: Context) {

    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // 앱의 오류는 우리가 잡을테니 너는 신경쓰지말아라. 하는것
    @SuppressLint("MissingPermission")  // 정확성, 보안, 성능, 유용성, 접근성, 최적화
    fun requestLocationUpdated(viewModel: LocationViewModel) {
        // 객체를 만들고 바로 오버라이드 하는것
         val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(latitude = it.latitude, longitude = it.longitude)
                    viewModel.updateLocation(location)
                }
            }
         }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        _fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun hasLocationPermission(context: Context):Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun reverseGeocodeLocation(locationData: LocationData) : String {
        val geocoder = Geocoder(context, Locale.KOREA)

        val coordinate = LatLng(locationData.latitude, locationData.longitude)
        val addresses: MutableList<Address>? = geocoder.getFromLocation( //// 위치에 맞는 주소가 여러개 일 수 있음 으로 리스트 (옛날 버전)
            coordinate.latitude, coordinate.longitude,
            1
        )
        return if (addresses?.isNotEmpty() == true) {
            addresses[0].getAddressLine(0)
        } else {
            return "주소를 찾을 수 없음."
        }

    }
}

//        if(
//            ContextCompat.
//            checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED) {
//            return true
//        } else {
//            false
//        }