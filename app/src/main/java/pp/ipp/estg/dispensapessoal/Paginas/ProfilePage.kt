package pp.ipp.estg.dispensapessoal.paginas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonsViewModels

@Preview
@Composable
fun PreviewProfilePage() {
    val person = PersonModel(999999999, "1134", "username", "joao", "alfredo", "exemplo@gmail.com", null)
    ProfilePage(person, rememberNavController())
}

@Composable
fun ProfilePage(person: PersonModel, navController: NavHostController) {
    var edit by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf(person.firstname) }
    var lastName by remember { mutableStateOf(person.lastName) }
    var email by remember { mutableStateOf(person.email) }
    var username by remember { mutableStateOf(person.username) }
    var telem by remember { mutableIntStateOf(person.telem) }
    var password by remember { mutableStateOf(person.password) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "profile image",
            tint = Color.Black,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${person.firstname} ${person.lastName}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "+351 ${person.telem}")

        Spacer(modifier = Modifier.height(4.dp))

        Column {

            //FirstName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Primeiro nome",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        enabled = edit,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            //LastName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Último nome",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        enabled = edit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            //Email
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        enabled = edit,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }

            //Username
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Username",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        enabled = edit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            //Telemovel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Nº telemóvel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = telem.toString(),
                        onValueChange = { telem = it.toInt() },
                        modifier = Modifier.weight(1f),
                        enabled = edit,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            }

            //Password
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Passowrd",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        enabled = edit,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        var clickBtn by remember { mutableStateOf(false) }
        Button(
            onClick = {
                edit = !edit
            },
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 10.dp, bottom = 10.dp)
                .fillMaxWidth()
        ) {
            if (!edit) {
                Text(text = "Editar Perfil")
            } else {
                clickBtn = true
                Text(text = "Atualizar Perfil")
            }
        }

        if (!edit && clickBtn) {
            val personsViewModels: PersonsViewModels = viewModel()

            val tempPerson = PersonModel(telem, password, username, firstName, lastName, email, person.pantries)
            personsViewModels.updatePerson(tempPerson)
            navController.navigate("Página Principal")
        }

    }
}