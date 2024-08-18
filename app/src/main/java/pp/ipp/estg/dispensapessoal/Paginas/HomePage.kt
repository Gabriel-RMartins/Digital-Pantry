package pp.ipp.estg.dispensapessoal.Paginas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel
import pp.ipp.estg.dispensapessoal.paginas.SettingsPage

@Preview
@Composable
fun previewCardPantry() {
    val pantry = PantryModel(1, "Dispensa Faculdade", null, null)

    CardPantry(pantry, navController = rememberNavController())
}

@Composable
fun CardPantry(pantry: PantryModel, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .clickable {
                navController.navigate("pantry/${pantry.pantry_id}")
            }
            .padding(top = 20.dp, bottom = 6.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
    ) {

        Image(
            painter = painterResource(id = R.drawable.pantry),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = pantry.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "members")

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = pantry.members?.size.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

            }
        }


    }
}


@Composable
fun HomePagePortait(personModel: PersonModel, navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(end = 20.dp)
                .weight(1f)
        ) {
            IconButton(
                onClick = { navController.navigate("Perfil") },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            ) {
                Text(
                    text = "Olá,\n${personModel.firstname} ${personModel.lastName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = { navController.navigate("Definições") },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "",
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate("MapaAPI") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Supermercados perto...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(20.dp))

                Icon(imageVector = Icons.Default.Storefront, contentDescription = "")
            }
        }


        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
        ) {
            MinhasDispensasPortait(pantries = personModel.pantries, navController = navController)
        }
    }
}

@Preview
@Composable
fun previewHomePage() {

    val pantry = PantryModel(1, "Dispensa Faculdade", null, null)
    val pantries = listOf(
        pantry,
        pantry
    )
}

@Composable
fun HomePage(personModel: PersonModel, navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.navigate("Perfil") },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            ) {
                Text(
                    text = "${stringResource(id = R.string.bemvindo)},\n${personModel.firstname} ${personModel.lastName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = { navController.navigate("Definições") },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "",
                    )
                }
            }
        }

        MinhasDispensas(pantries = personModel.pantries, navController = navController)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("MapaAPI") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Supermercados perto...",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(20.dp))

            Icon(imageVector = Icons.Default.Storefront, contentDescription = "")
        }

    }
}

@Preview
@Composable
fun previewMinhasDispensas() {

    val pantry = PantryModel(1, "Dispensa Faculdade", null, null)
    val pantries = listOf(
        pantry,
        pantry
    )

    //MinhasDispensas(pantries, navController = rememberNavController())
}

@Composable
fun MinhasDispensasPortait(pantries: List<Int>?, navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "As minhas dispensas...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = { navController.navigate("AddPantry") }
        ) {
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "add pantry")
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        val pantriesViewModels: PantriesViewModels = viewModel()
        pantries?.forEach() { pantry ->
            Spacer(modifier = Modifier.width(4.dp))
            pantriesViewModels.getOnePantry(pantry)
                .observeAsState().value?.let { CardPantry(pantry = it, navController) }
        }
    }

}

@Composable
fun MinhasDispensas(pantries: List<Int>?, navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.minhaPantry),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = { navController.navigate("AddPantry") }
        ) {
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "add pantry")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        val pantriesViewModels: PantriesViewModels = viewModel()
        pantries?.forEach() { pantry ->
            Spacer(modifier = Modifier.width(4.dp))
            pantriesViewModels.getOnePantry(pantry)
                .observeAsState().value?.let { CardPantry(pantry = it, navController) }
        }
    }

}
