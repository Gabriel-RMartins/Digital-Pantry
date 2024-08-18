package pp.ipp.estg.dispensapessoal.Paginas

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
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
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel
import pp.ipp.estg.dispensapessoal.paginas.MapaApi
import pp.ipp.estg.dispensapessoal.pantry.ProductViewModels

@Composable
fun TopBarProductPage(navController: NavHostController, title: String) {
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
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewAddProductPage() {
    val pantry = PantryModel(0, "null", null, null)
    AddProductPage(navController = rememberNavController(), pantry)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductPage(navController: NavHostController, pantry: PantryModel) {
    var nome by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var qtd by remember { mutableFloatStateOf(0.0f) }
    var type by remember { mutableStateOf("") }

    val pantriesViewModels: PantriesViewModels = viewModel()

    var isSelected by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {

        TopBarProductPage(navController = navController, "Adicionar Produto")

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pantry),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth() // Preenches a largura disponível
                    .height(200.dp) // Definess a altura da imagem
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nome Produto",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text(text = "Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Marca:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text(text = "Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            //UnidadeMedidaProduto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Unidade de Medida",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = { isExpanded = it },
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        TextField(
                            value = type,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Kg") },
                                onClick = {
                                    type = "Kg"
                                    isExpanded = false
                                    isSelected = type
                                    qtd = 0.0f
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(text = "Uni.") },
                                onClick = {
                                    type = "Uni."
                                    isExpanded = false
                                    isSelected = type
                                    qtd = 0.0f
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Quantidade:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Button(
                            onClick = {
                                if (isSelected.equals("Kg"))
                                    qtd = maxOf(qtd - 0.1f, 0.1f)
                                else if (isSelected.equals("Uni."))
                                    qtd = maxOf(qtd - 1.0f, 1.0f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "-")
                        }

                        Text(
                            text = "%.1f".format(qtd),
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Button(
                            onClick = {
                                if (isSelected.equals("Kg"))
                                    qtd += 0.1f
                                else if (isSelected.equals("Uni."))
                                    qtd += 1.0f
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "+")
                        }
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        val productViewModels: ProductViewModels = viewModel()
        val leaderboardViewModels: LeaderboardViewModels = viewModel()
        val count = productViewModels.getallProducts().observeAsState().value?.size?.plus(1)
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

        if (click) {
            val newProduct = count?.let { ProductModel(it, nome, brand, qtd, type) }
            if (newProduct != null) {
                productViewModels.insertProduct(newProduct)
                pantriesViewModels.addProductToPantry(pantry, newProduct)

                leaderboardViewModels.getPantryLeaderboard(pantry.pantry_id) { pantryLeaderboard ->
                    Log.d("LEADERBOARD PANTRY UPDATE", pantryLeaderboard.toString())
                    if (pantryLeaderboard != null) {
                        val temp = pantryLeaderboard.copy(numProducts = pantryLeaderboard.numProducts + 1)
                        leaderboardViewModels.setPantryLeaderboard(temp)
                    }
                }
            }

            navController.popBackStack("pantry/${pantry.pantry_id}", true)
        }

    }
}

@Preview
@Composable
fun PreviewViewProductPage() {

    val products = listOf(
        ProductModel(1, "produto1", "LIDL", 2f, "kg"),
        ProductModel(2, "produto2", "LIDL", 0.2f, "kg"),
        ProductModel(3, "produto3", "LIDL", 2f, "uni"),
        ProductModel(4, "produto4", "LIDL", 2f, "kg"),
        ProductModel(4, "produto5", "ALDI", 2f, "uni")
    )
    val pantry = PantryModel(0, "Exemplo", products, null)

    ViewProductPage(products[0], pantry, navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProductPage(product: ProductModel, pantry: PantryModel, navController: NavHostController) {
    var name by remember { mutableStateOf(product.name) }
    var brand by remember { mutableStateOf(product.brand) }
    var qtd by remember { mutableFloatStateOf(product.qtd) }
    var type by remember { mutableStateOf(product.qtd_type) }

    var edit by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(edit) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {

        TopBarProductPage(navController = navController, product.name)

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pantry),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nome Produto",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextField(
                value = name,
                onValueChange = { name = it },
                readOnly = !edit,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Marca:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextField(
                value = brand,
                onValueChange = { brand = it },
                readOnly = !edit,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Unidade de Medida",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = { isExpanded = it },
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        TextField(
                            value = type,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Kg") },
                                onClick = {
                                    type = "Kg"
                                    isExpanded = false
                                    isSelected = type
                                    qtd = product.qtd
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(text = "Uni.") },
                                onClick = {
                                    type = "Uni."
                                    isExpanded = false
                                    isSelected = type
                                    qtd = product.qtd
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Quantidade:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Button(
                            onClick = {
                                if (isSelected == "Kg")
                                    qtd = maxOf(qtd - 0.1f, 0.1f)
                                else if (isSelected == "Uni.")
                                    qtd = maxOf(qtd - 1.0f, 1.0f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "-")
                        }

                        Text(
                            text = "%.1f".format(qtd),
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Button(
                            onClick = {
                                if (isSelected == "Kg")
                                    qtd += 0.1f
                                else if (isSelected == "Uni.")
                                    qtd += 1.0f
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "+")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        var clickBtn by remember { mutableStateOf(false) }
        Button(
            onClick = {
                edit = !edit
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth()
        ) {
            if (!edit)
                Text(text = "Editar Produto")
            else {
                Text(text = "Atualizar")
                clickBtn = true
            }
        }

        if (!edit && clickBtn) {
            val productViewModels: ProductViewModels = viewModel()
            val pantriesViewModels: PantriesViewModels = viewModel()

            val tempProduct = ProductModel(product.product_id, name, brand, qtd, type)
            productViewModels.updatetProduct(tempProduct)
            pantriesViewModels.updateProductFromPantry(pantry, tempProduct)

            navController.popBackStack("pantry/${pantry.pantry_id}", true)
        }

    }
}