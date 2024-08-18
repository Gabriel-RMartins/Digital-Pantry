package pp.ipp.estg.dispensapessoal

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pp.ipp.estg.dispensapessoal.Service.LocationUpdateService
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.personDB.PersonsViewModels
import pp.ipp.estg.dispensapessoal.paginas.LoginPage
import pp.ipp.estg.dispensapessoal.paginas.MyNavigatonDrawer
import pp.ipp.estg.dispensapessoal.paginas.SignupPage
import pp.ipp.estg.dispensapessoal.paginas.forgotpassword
import pp.ipp.estg.dispensapessoal.pantry.ProductViewModels
import pp.ipp.estg.dispensapessoal.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var darkMode by remember {
                mutableStateOf(false)
            }
            AppTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    startService(Intent(this, LocationUpdateService::class.java))
                    darkMode = lightSensorComponent()
                    MyNavHost()

                }
            }
        }
    }
}

@Composable
fun deleteRoomData(){
    val pantriesViewModels: PantriesViewModels = viewModel()
    val personsViewModels: PersonsViewModels = viewModel()
    val productViewModels: ProductViewModels = viewModel()

    val personsList by personsViewModels.getallPersons().observeAsState()
    val pantriesList by pantriesViewModels.getallPantries().observeAsState()
    val productsList by productViewModels.getallProducts().observeAsState()

    LaunchedEffect(key1 = Unit){
        pantriesList?.forEach {
            pantriesViewModels.deletePantry(it)
        }

        personsList?.forEach {
            personsViewModels.deletePerson(it)
        }

        productsList?.forEach {
            productViewModels.deleteProduct(it)
        }
    }
}

@Composable
fun MyNavHost() {
    val navController = rememberNavController()
    val personsViewModels: PersonsViewModels = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController = navController) }

        composable("forgotpassw") { forgotpassword(navController) }

        composable("signup") { SignupPage(navController) }

        composable("Página Principal/{person_telem}") { backStackEntry ->
            val person = backStackEntry.arguments?.getString("person_telem")
                ?.let { personsViewModels.getOnePerson(it.toInt()).observeAsState().value }

            if (person != null) {
                MyNavigatonDrawer(person = person, navController)

            }
        }
    }
}

@Composable
fun lightSensorComponent(): Boolean {

    val darkMode = remember {
        mutableStateOf(false)
    }

    val ctx = LocalContext.current
    val sensorStatus = remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val sensorManager: SensorManager =
            ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)


        val lightSensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    sensorStatus.value = event.values[0].toInt()
                }
            }
        }

        sensorManager.registerListener(
            lightSensorEventListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager.unregisterListener(lightSensorEventListener)
        }

    }

    darkMode.value = sensorStatus.value < 20000

    return darkMode.value
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        MyNavHost()
    }
}