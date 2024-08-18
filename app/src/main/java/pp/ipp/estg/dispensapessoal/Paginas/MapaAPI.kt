package pp.ipp.estg.dispensapessoal.paginas

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.Retrofit.RetrofitHelper
import pp.ipp.estg.dispensapessoal.api.Feature
import pp.ipp.estg.dispensapessoal.api.GeoapifyApiService
import pp.ipp.estg.dispensapessoal.api.Location
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Preview
@Composable
fun PreviewMapaApi(){
    MapaApi(navController = rememberNavController())
}

@Composable
fun MapaApi(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val permission_given = remember {
            mutableStateOf(0)
        }
        val ctx = LocalContext.current
        if (ActivityCompat.checkSelfPermission(
                ctx,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                ctx,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permission_given.value = 2
        }
        val permissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    permission_given.value += 1
                }
            }
        LaunchedEffect(key1 = "Permission") {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        Column {
            Mapa()

        }
    }
}

@Composable
fun Mapa() {
    var currentPosition by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var supermarkets by remember { mutableStateOf<List<Feature>>(emptyList()) }



    LocationUpdates { newLocation ->
        currentPosition = newLocation
    }

    // Passe uma função de retorno de chamada para atualizar supermarkets em PointsList
    PointsList(currentPosition) { updatedSupermarkets ->
        supermarkets = updatedSupermarkets
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        val customIcon = createCustomIcon(R.drawable.loc)

        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentPosition, 15f)

        // Adicione marcadores para os supermercados
        supermarkets.forEach { supermarket ->
            MarkerInfoWindow(
                state = MarkerState(position = LatLng(supermarket.geometry.coordinates[1], supermarket.geometry.coordinates[0])),
                title = supermarket.properties.name,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        // Adicione marcador para a localização atual
        Marker(
            state = MarkerState(position = currentPosition),
            title = "Localização Atual",
            icon = customIcon
        )
    }
}

@Composable
private fun createCustomIcon(iconResource: Int): BitmapDescriptor {
    val density = LocalDensity.current.density
    val ctx = LocalContext.current
    val bitmap =
        Bitmap.createBitmap((48 * density).toInt(), (48 * density).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val icon = ContextCompat.getDrawable(ctx, iconResource)
    icon?.setBounds(0, 0, bitmap.width, bitmap.height)
    icon?.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}



@SuppressLint("MissingPermission")
@Composable
fun LocationUpdates(onLocationChanged: (LatLng) -> Unit) {
    val text = remember { mutableStateOf("") }
    val ctx = LocalContext.current
    DisposableEffect(Unit) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            text.value = "Latitude: ${it.latitude} Longitude: ${it.longitude}"
            onLocationChanged(LatLng(it.latitude, it.longitude))
        }.addOnFailureListener {
            text.value = "Unable to get locations"
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000,
        ).setMinUpdateIntervalMillis(1000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locations: LocationResult) {
                for (location in locations.locations) {
                    text.value = "Latitude: ${location.latitude} Longitude: ${location.longitude}"
                    onLocationChanged(LatLng(location.latitude, location.longitude))
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        onDispose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    //Text(text = text.value)
}
@Composable
fun PointsList(currentLocation: LatLng, onPointsListChanged: (List<Feature>) -> Unit) {
    var pointsList by remember { mutableStateOf<List<Feature>>(emptyList()) }

    val tourApi = RetrofitHelper.getInstance().create(GeoapifyApiService::class.java)
    tourApi.getSupermarkets("commercial.supermarket", "named", "circle:${currentLocation.longitude},${currentLocation.latitude},5000", "proximity:${currentLocation.longitude},${currentLocation.latitude}", 20, "08d89b79113e4bd696deaa5ba43ab38c")
        .enqueue(object : Callback<Location> {
            override fun onResponse(call: Call<Location>, response: Response<Location>) {
                // Atualize a pointsList quando a resposta for recebida
                val location = response.body()
                pointsList = location?.features ?: emptyList()
                // Notifique o chamador com a pointsList atualizada
                onPointsListChanged(pointsList)
            }

            override fun onFailure(call: Call<Location>, t: Throwable) {
                // Lide com a falha se necessário
            }
        })

}
