package pp.ipp.estg.dispensapessoal.Service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import pp.ipp.estg.dispensapessoal.MainActivity
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.Retrofit.RetrofitHelper
import pp.ipp.estg.dispensapessoal.api.Feature
import pp.ipp.estg.dispensapessoal.api.GeoapifyApiService
import pp.ipp.estg.dispensapessoal.api.Location
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt

class LocationViewModel : ViewModel() {
    val notificationContent = MutableLiveData("Updating user location")
}

class LocationUpdateService : Service() {

    private lateinit var locationManager: LocationManager
    private var supermarkets: List<Feature> = emptyList()

    private var currentLocation: LatLng? = null


    companion object {
        const val NOTIFICATION_CHANNEL_ID = "location_update_channel"
        const val NOTIFICATION_ID = 1
        const val SUPERMARKET_PROXIMITY_THRESHOLD = 500 // 500 meters
    }


    private lateinit var viewModel: LocationViewModel
    override fun onCreate() {
        super.onCreate()

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(LocationViewModel::class.java)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startForeground(NOTIFICATION_ID,
//            viewModel.notificationContent.value?.let { buildForegroundNotification(it) })

        if (checkLocationPermissions()) {
            startLocationUpdates()
        }

        supermarkets = mutableListOf<Feature>().apply {
            currentLocation?.let { location ->
                getSupermarkets(location) { updatedSupermarkets ->
                    Log.d(TAG, "Supermarkets atualizados: $updatedSupermarkets")
                    this.addAll(updatedSupermarkets)
                }
            }
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_STICKY_COMPATIBILITY
        }

        return START_STICKY
    }

    private fun checkLocationPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    private var lastLocationUpdateNotificationTime: Long = 0
    private val MIN_LOCATION_UPDATE_NOTIFICATION_INTERVAL = 10 * 60 * 1000 // 5 minutos

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5 * 60 * 1000,
        ).setMinUpdateIntervalMillis(5 * 60 * 1000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locations: LocationResult) {
                for (location in locations.locations) {
                    Log.d(TAG, "Location changed: $location")
                    if (System.currentTimeMillis() - lastLocationUpdateNotificationTime > MIN_LOCATION_UPDATE_NOTIFICATION_INTERVAL) {
                        viewModel.notificationContent.value?.let {
                            startForeground(NOTIFICATION_ID, buildForegroundNotification(it))
                            lastLocationUpdateNotificationTime = System.currentTimeMillis()
                        }
                    }

                    currentLocation = LatLng(location.latitude, location.longitude)
                    getSupermarkets(currentLocation!!) { updatedSupermarkets ->
                        Log.d(TAG, "Supermarkets atualizados: $updatedSupermarkets")
                        supermarkets = updatedSupermarkets
                    }

                    checkSupermarketProximity(currentLocation!!)
                }
            }
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            it?.let { location ->
                Log.d(TAG, "Last known location: $location")
                // Atualize a localização atual e obtenha os supermercados
                currentLocation = LatLng(location.latitude, location.longitude)
                getSupermarkets(currentLocation!!) { updatedSupermarkets ->
                    Log.d(TAG, "Supermarkets atualizados: $updatedSupermarkets")
                    supermarkets = updatedSupermarkets
                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "Unable to get last known location", it)
        }
    }

    private fun buildForegroundNotification(content: String): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("A detetar a sua localização")
            .setContentText(content)
            .setSmallIcon(R.drawable.pantry)
            .build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Location Update Channel"
            val descriptionText = "Channel for location updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private val TAG = "LocationUpdateService"

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
            // Implemente o que precisa ser feito quando a localização é alterada
            Log.d(TAG, "Location destroy: $location")
            checkSupermarketProximity(LatLng(location.latitude, location.longitude))
        }
    }
    private var lastNotifiedSupermarket: Feature? = null

    private var lastNotificationTime: Long = 0
    private val MIN_NOTIFICATION_INTERVAL = 5 * 60 * 1000 // 5 minutos

    private fun checkSupermarketProximity(userLocation: LatLng) {
        for (supermarket in supermarkets) {
            val supermarketLocation = LatLng(
                supermarket.geometry.coordinates[1],
                supermarket.geometry.coordinates[0]
            )
            val distance = calculateDistance(userLocation, supermarketLocation)

            if (distance <= SUPERMARKET_PROXIMITY_THRESHOLD) {
                if (supermarket != lastNotifiedSupermarket && System.currentTimeMillis() - lastNotificationTime > MIN_NOTIFICATION_INTERVAL) {
                    lastNotifiedSupermarket = supermarket
                    lastNotificationTime = System.currentTimeMillis()
                    supermarket.properties.name?.let { showProximityNotification(it) }
                    Log.d("Location TESTE", distance.toString())
                }
                break
            }

            if (locationChangedSignificantly(userLocation)) {
                lastNotifiedSupermarket = null
            }
        }
    }



    private fun locationChangedSignificantly(newLocation: LatLng): Boolean {
        if (currentLocation == null) {
            return true
        }

        val distance = calculateDistance(newLocation, currentLocation!!)
        Log.d(TAG,"Location TESTE3 $distance.toString()")


        val significantDistanceThreshold = 500.0

        Log.d(TAG,"Location TESTE4 $distance.toString()")
        return distance > significantDistanceThreshold
    }


    private fun calculateDistance(location1: LatLng, location2: LatLng): Double {
        val earthRadius = 6371000.0 // in meters
        val dLat = Math.toRadians(location2.latitude - location1.latitude)
        val dLon = Math.toRadians(location2.longitude - location1.longitude)
        val lat1 = Math.toRadians(location1.latitude)
        val lat2 = Math.toRadians(location2.latitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val PROXIMITY_NOTIFICATION_ID = 2



    private fun showProximityNotification(supermarketName: String) {
        Log.d(TAG, "Proximity notification: $supermarketName")

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Supermercado próximo")
            .setContentText("Está próximo de $supermarketName")
            .setSmallIcon(R.drawable.loc)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(PROXIMITY_NOTIFICATION_ID, notification)
    }


    private fun getSupermarkets(
        currentLocation: LatLng,
        onSupermarketsFetched: (List<Feature>) -> Unit
    ) {
        val tourApi = RetrofitHelper.getInstance().create(GeoapifyApiService::class.java)
        tourApi.getSupermarkets(
            "commercial.supermarket",
            "named",
            "circle:${currentLocation.longitude},${currentLocation.latitude},2500",
            "proximity:${currentLocation.longitude},${currentLocation.latitude}",
            20,
            "08d89b79113e4bd696deaa5ba43ab38c"
        ).enqueue(object : Callback<Location> {
            override fun onResponse(call: Call<Location>, response: Response<Location>) {
                val location = response.body()
                val supermarkets = location?.features ?: emptyList()
                onSupermarketsFetched(supermarkets)
            }

            override fun onFailure(
                call: Call<pp.ipp.estg.dispensapessoal.api.Location>,
                t: Throwable
            ) {
                Log.e(TAG, "Erro ao obter supermercados: ${t.message}", t)
                onSupermarketsFetched(emptyList())
            }
        })
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

}

