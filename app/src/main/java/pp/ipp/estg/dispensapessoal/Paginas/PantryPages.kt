package pp.ipp.estg.dispensapessoal.paginas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.database.leaderboard.LeaderboardViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel
import pp.ipp.estg.dispensapessoal.pantry.ProductViewModels

@Preview
@Composable
fun PreviewPantryPage(){
    val products = listOf(
        ProductModel(1, "produto1", "LIDL", 2f, "kg"),
        ProductModel(2, "produto2", "LIDL", 0.2f, "kg"),
        ProductModel(3, "produto3", "LIDL", 2f, "uni"),
        ProductModel(4, "produto4", "LIDL", 2f, "kg"),
        ProductModel(4, "produto5", "ALDI", 2f, "uni")
    )
    val pantry = PantryModel(1, "Dispensa Faculdade", products, null)
    PantryPage(pantry, navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryPage(pantry: PantryModel, navController: NavHostController) {

    var searchText by remember { mutableStateOf("") }
    var productList by remember { mutableStateOf(emptyList<ProductModel>()) }

    LaunchedEffect(pantry.products) {
        productList = pantry.products ?: emptyList()
    }

    // Filtrar a lista de produtos com texto de pesquisa
    val filteredProducts = productList.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBarPantryPage(pantry = pantry, navController)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 30.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                placeholder = { Text(text = "Search...") },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Spacer(modifier = Modifier.height(6.dp))
            filteredProducts.forEach { product ->
                CardItem(product = product, pantry, navController)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { navController.navigate("AddProduct/${pantry.pantry_id}") },
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}

@Composable
fun TopBarPantryPage(pantry: PantryModel, navController: NavHostController) {
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
                text = pantry.name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.clickable { navController.navigate("members") }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(end = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "",
                )

                Text(
                    text = pantry.members?.size.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
        }
    }
}

@Composable
fun CardItem(product: ProductModel, pantry: PantryModel, navController: NavHostController) {
    val productViewModels: ProductViewModels = viewModel()
    val pantriesViewModels: PantriesViewModels = viewModel()
    val leaderboardViewModels: LeaderboardViewModels = viewModel()

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .size(height = 120.dp, width = Dp.Infinity)
            .padding(10.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.produto),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = product.brand,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                var click by remember { mutableStateOf(false) }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .fillMaxHeight()
                ) {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            click = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }

                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            navController.navigate("ViewProduct/${product.product_id}/${pantry.pantry_id}")
                            Log.d("FIREBASE", "PantryPage: ${product.product_id} ${pantry.pantry_id} ${pantry.name}")
                        },
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )
                    }
                }

                if(click){
                    productViewModels.deleteProductFirebase(product)
                    pantriesViewModels.deleteProductFromPantry(pantry, product)

                    leaderboardViewModels.getPantryLeaderboard(idPantry = pantry.pantry_id){
                        it?.let { pantryLeaderboard ->
                            val temp = pantryLeaderboard.copy(numProducts = pantryLeaderboard.numProducts - 1)
                            leaderboardViewModels.setPantryLeaderboard(temp)
                        }

                    }

                    click = false
                }
            }
        }
    }
}