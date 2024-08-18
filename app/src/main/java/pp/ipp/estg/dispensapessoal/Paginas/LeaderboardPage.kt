package pp.ipp.estg.dispensapessoal.Paginas

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.database.leaderboard.LeaderboardViewModels
import pp.ipp.estg.dispensapessoal.database.leaderboard.PantryLeaderboard
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel

@Composable
fun LeaderboardPage() {
    val leaderboardViewModels: LeaderboardViewModels = viewModel()

    var list by remember { mutableStateOf<List<PantryLeaderboard>?>(null) }

    leaderboardViewModels.getFirebaseLeaderboard {
        list = it
    }

    if (list != null) {
        val sortedList = list?.sortedByDescending { it.numProducts }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp, top = 20.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            Text(
                text = "LEADERBOARD",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(id = R.string.numberproducts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Divider(
                color = Color.Black,
                thickness = 2.dp,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            sortedList?.forEachIndexed { index, pantry ->
                if (index < (sortedList.size ?: 0)) {
                    CardPantryLeaderboard(
                        place = index + 1,
                        idPantry = pantry.idPantry,
                        pantryLeaderboard = sortedList[index]
                    )
                }
            }

        }
    }
}


@Composable
fun CardPantryLeaderboard(place: Int, idPantry: Int, pantryLeaderboard: PantryLeaderboard) {
    var pantry by remember { mutableStateOf<PantryModel?>(null) }
    val pantriesViewModels: PantriesViewModels = viewModel()
    pantriesViewModels.getFirebasePantry(idPantry){
        pantry = it
    }

    if(pantry != null){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "$place .",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .size(height = 80.dp, width = Dp.Infinity)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .fillMaxSize()
                ) {
                    pantry?.name?.let {
                        Text(
                            text = it,
                            fontSize = 22.sp
                        )
                    }

                    Text(
                        text = pantryLeaderboard.numProducts.toString(),
                        fontSize = 22.sp,
                    )
                }

            }
        }
    }
}