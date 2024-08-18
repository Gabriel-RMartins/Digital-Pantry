package pp.ipp.estg.dispensapessoal.paginas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pp.ipp.estg.dispensapessoal.R.*
import pp.ipp.estg.dispensapessoal.R.color.*
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonsViewModels

@Preview
@Composable
fun PreviewMembersPages() {
    val person = PersonModel(0, "", "", "", "", "", null)
    val pantry = PantryModel(0, "", null, null)

    MembersPages(person, pantry, navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersPages(person: PersonModel, pantry: PantryModel, navController: NavHostController) {
    val personsViewModels: PersonsViewModels = viewModel()
    val pantriesViewModels: PantriesViewModels = viewModel()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Members",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            Column{
                Button(
                    onClick = {
                        pantriesViewModels.deletePantry(pantry)
                        pantriesViewModels.deleteMemberFromPantry(pantry, person)
                        personsViewModels.deletePantryfromPerson(person, pantry)
                        navController.navigate("Página Principal")
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())

                ) {
                    Text(
                        text = "Sair",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                }

                pantry.members?.forEach { member ->
                    CardMember(person = person, member = member)
                }
            }


            var isDialogVisible by remember { mutableStateOf(false) }
            var inputText by remember { mutableStateOf("") }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { isDialogVisible = true },
                ) {
                    Icon(Icons.Filled.Add, "Floating action button.")
                }
            }

            var find by remember { mutableStateOf(false) }
            val pantriesViewModels: PantriesViewModels = viewModel()
            // AlertDialog
            if (isDialogVisible) {
                AlertDialog(
                    onDismissRequest = {
                        isDialogVisible = false
                    },
                    title = {
                        Text("Adicionar Membro")
                    },
                    text = {
                        // Campo de texto para entrada
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text("Nº Telemóvel do novo membro") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                                .fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                find = true
                                isDialogVisible = false
                            }
                        ) {
                            Text("Adicionar")
                        }
                    }
                )

                if (find) {
                    if (inputText.isNotBlank() && inputText.isNotEmpty()) {
                        Log.d("FIREBASE ADD MEMBER", "ENTROU")
                        val tempPerson: PersonModel? =
                            personsViewModels.getOnePerson(inputText.toInt()).observeAsState().value
                        Log.d(
                            "FIREBASE TempPerson",
                            tempPerson.toString() + "    input: " + inputText.toInt()
                        )

                        if (tempPerson != null) {
                            personsViewModels.insertPantryToPerson(tempPerson, pantry)
                            pantriesViewModels.addMemberToPantry(pantry, tempPerson)
                            Log.d("TempPersons", tempPerson.toString())
                            find = false
                        }
                        isDialogVisible = false
                    }
                    Log.d("FIREBASE ADD MEMBER", "NAO ENTROU")
                }
            }
            //TODO - ainda nao esta a funcionar
        }
    }

}

@Composable
fun CardMember(person: PersonModel, member: PersonModel) {

    val btn by remember { mutableStateOf(!(person.telem == member.telem)) }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .size(height = 100.dp, width = Dp.Infinity)
            .padding(horizontal = 20.dp, vertical = 6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Image(
                    painter = painterResource(id = drawable.person),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(
                    text = "${member.firstname} ${member.lastName}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp)
            ) {

                var isPermissionGranted by remember { mutableStateOf(false) }
                val context = rememberContext()

                val requestPermissionLauncher = rememberLauncher {
                    isPermissionGranted = it
                }
                Button(
                    enabled = btn,
                    onClick = {
                        val phoneNumber =
                            "tel:${member.telem}"
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber))

                        // Verifica se a permissão CALL_PHONE está concedida antes de realizar a chamada
                        if (isPermissionGranted || ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            context.startActivity(intent)
                        } else {
                            // Solicite permissão ao usuário se a permissão ainda não estiver concedida
                            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                        }
                    },
                    modifier = Modifier
                        .size(height = 40.dp, width = 80.dp)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "")
                }

                Spacer(modifier = Modifier.height(2.dp))

                Button(
                    enabled = btn,
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(height = 40.dp, width = 80.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                }
            }
        }


    }
}

@Composable
fun rememberContext(): ComponentActivity {
    val context = LocalContext.current
    return (context as? ComponentActivity)
        ?: error("CompositionLocal LocalContext not found in the hierarchy. Make sure to use this composable within a Compose hierarchy hosted by a ComponentActivity.")
}

@Composable
fun rememberLauncher(onResult: (Boolean) -> Unit): ActivityResultLauncher<String> {
    return rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        onResult(result)
    }
}