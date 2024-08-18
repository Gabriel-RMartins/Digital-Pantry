package pp.ipp.estg.dispensapessoal.paginas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.database.leaderboard.LeaderboardViewModels
import pp.ipp.estg.dispensapessoal.database.leaderboard.PantryLeaderboard
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonsViewModels


@Composable
fun TopBarAddPantryPage(navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }

            Text(
                text = "Adicionar Dispensa",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun AddPantry(person: PersonModel, navController: NavHostController) {
    var dispensa by remember { mutableStateOf("") }
    val personsViewModels: PersonsViewModels = viewModel()
    val leaderboardViewModels: LeaderboardViewModels = viewModel()
    val pantriesViewModels: PantriesViewModels = viewModel()
    var count = 0

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TopBarAddPantryPage(navController = navController)

            Image(
                painter = painterResource(id = R.drawable.pantry),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth() // Preenches a largura disponível
                    .height(200.dp) // Definess a altura da imagem
                    .padding(top = 10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Nome Dispensa",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = dispensa,
                onValueChange = { dispensa = it },
                label = { Text(text = "Nome") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        var click by remember { mutableStateOf(false) }
        Button(
            onClick = {
                click = true
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Adicionar")
        }

        var insert by remember { mutableStateOf(true) }
        if (click) {
            pantriesViewModels.getFirebasePantriesCount {
                count = it + 1
                if(insert){
                    val pantry = PantryModel(count, dispensa, emptyList(), emptyList())
                    val pantryLeaderboard =
                        pantry.products?.size?.let { PantryLeaderboard(0, pantry.pantry_id, it) }

                    if (pantryLeaderboard != null) {
                        leaderboardViewModels.setPantryLeaderboard(pantryLeaderboard)
                        pantriesViewModels.insertPantry(pantry)
                        pantriesViewModels.addMemberToPantry(pantry, person)
                        personsViewModels.insertPantryToPerson(person, pantry)
                    }
                    insert = false
                    navController.popBackStack()
                }
            }

            click = false
        }


    }
}


@Preview
@Composable
fun previewAddDispensa() {
    //AddPantry(navController = rememberNavController())
}