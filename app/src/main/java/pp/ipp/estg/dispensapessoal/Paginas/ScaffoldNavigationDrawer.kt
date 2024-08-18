package pp.ipp.estg.dispensapessoal.paginas

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pp.ipp.estg.dispensapessoal.Paginas.AddProductPage
import pp.ipp.estg.dispensapessoal.Paginas.HomePage
import pp.ipp.estg.dispensapessoal.Paginas.HomePagePortait
import pp.ipp.estg.dispensapessoal.Paginas.LeaderboardPage
import pp.ipp.estg.dispensapessoal.Paginas.ViewProductPage
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.pantry.ProductViewModels

@Composable
fun MyLogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmação") },
        text = { Text("Deseja sair da conta?") },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Sim")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Não")
            }
        }
    )
}

@Composable
fun MyNavigatonDrawer(person: PersonModel, loginNavController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val drawerItemList = prepareNavigationDrawerItems()
    var selectedItem by remember { mutableStateOf(drawerItemList[0]) }

    ModalNavigationDrawer(
        gesturesEnabled = false,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                drawerItemList.forEach { item ->
                    MyDrawerItem(
                        item,
                        selectedItem,
                        { selectedItem = it },
                        navController,
                        drawerState
                    )
                }
            }
        },
        content = {
            MyScaffold(
                person = person,
                drawerState = drawerState,
                navController = navController,
                loginNavController = loginNavController
            )
        }
    )
}

@Composable
fun MyDrawerItem(
    item: NavigationDrawerData,
    selectedItem: NavigationDrawerData,
    updateSelected: (i: NavigationDrawerData) -> Unit,
    navController: NavHostController,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()
    NavigationDrawerItem(
        icon = { Icon(imageVector = item.icon, contentDescription = null) },
        label = { Text(text = item.label) },
        selected = (item == selectedItem),
        onClick = {
            coroutineScope.launch {
                navController.navigate(item.label)
                drawerState.close()
            }
            updateSelected(item)
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
fun MyScaffold(
    person: PersonModel,
    drawerState: DrawerState,
    navController: NavHostController,
    loginNavController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    var topBarVisible by remember { mutableStateOf(true) }

    fun setTopBarVisible(visible: Boolean) {
        topBarVisible = visible
    }

    Scaffold(
        topBar = {
            if (topBarVisible) {
                MyTopAppBar {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                MyScaffoldContent(person,navController, loginNavController,::setTopBarVisible)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(onNavIconClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Dispensa Pessoal") },
        navigationIcon = {
            IconButton(
                onClick = {
                    onNavIconClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Items"
                )
            }
        },
        actions = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    )
}

@Composable
fun MyScaffoldContent(
    person: PersonModel,
    navController: NavHostController,
    loginNavController: NavHostController,
    setTopBarVisible: (x: Boolean) -> Unit,
) {
    val productViewModels: ProductViewModels = viewModel()
    val pantriesViewModels: PantriesViewModels = viewModel()
    var pantry by remember { mutableStateOf<PantryModel?>(null) }

    val configuration = LocalConfiguration.current
    val portait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    NavHost(navController = navController, startDestination = "Página Principal") {
        composable("login") {
            LoginPage(navController)
            setTopBarVisible(false)
        }

        composable("Sair da conta"){
            MyLogoutConfirmationDialog(
                onConfirm = { loginNavController.navigate("login") },
                onDismiss = { navController.popBackStack() }
            )
        }

        composable("Página Principal") {
            if(portait){
                HomePage(person, navController)
                setTopBarVisible(true)
            } else {
                HomePagePortait(person, navController)
                setTopBarVisible(true)
            }


        }

        composable("Leaderboard") {
            LeaderboardPage()
            setTopBarVisible(true)
        }

        composable("MapaAPI") {
            MapaApi(navController)
            setTopBarVisible(true)
        }

        composable("Perfil") {
            ProfilePage(person, navController)
            setTopBarVisible(true)
        }

        composable("Definições") {
            SettingsPage(person)
            setTopBarVisible(true)
        }

        composable("AddPantry") {
            AddPantry(person, navController)
            setTopBarVisible(false)
        }

        composable("pantry/{id}") { backStackEntry ->
            pantry = backStackEntry.arguments?.getString("id")?.toInt()
                ?.let { pantriesViewModels.getOnePantry(it).observeAsState().value }

            pantry?.let { PantryPage(pantry = it, navController = navController) }
            setTopBarVisible(false)
        }

        composable("members") {
            pantry?.let { pantry -> MembersPages(person, pantry, navController) }
            setTopBarVisible(false)
        }

        composable("AddProduct/{id}") {backStackEntry ->
            pantry = backStackEntry.arguments?.getString("id")?.toInt()
                ?.let { pantriesViewModels.getOnePantry(it).observeAsState().value }
            pantry?.let { AddProductPage(navController, it) }
            setTopBarVisible(false)

        }

        composable("ViewProduct/{id}/{idPantry}") { backStackEntry ->
            val idPantry = backStackEntry.arguments?.getString("idPantry")?.toInt()
            backStackEntry.arguments?.getString("id")?.toInt()?.let {
                productViewModels.getOneProduct(it).observeAsState().value?.let { product ->
                    pantry = idPantry?.let { id -> pantriesViewModels.getOnePantry(id).observeAsState().value }
                    if (idPantry != null) {
                        pantry?.let { pantry ->
                            Log.d("Pantry", pantry.toString())
                            ViewProductPage(
                                product, pantry,navController
                            )
                        }
                    }
                }
            }
            setTopBarVisible(false)
        }
    }
}

private fun prepareNavigationDrawerItems(): List<NavigationDrawerData> {
    val drawerItemsList = arrayListOf<NavigationDrawerData>()
    drawerItemsList.add(NavigationDrawerData(label = "Página Principal", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Leaderboard", icon = Icons.Filled.Leaderboard))
    drawerItemsList.add(NavigationDrawerData(label = "Perfil", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Definições", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Sair da conta", icon = Icons.Filled.Logout))

    return drawerItemsList
}

data class NavigationDrawerData(val label: String, val icon: ImageVector)