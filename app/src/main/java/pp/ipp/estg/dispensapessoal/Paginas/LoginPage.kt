package pp.ipp.estg.dispensapessoal.paginas

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import pp.ipp.estg.dispensapessoal.R
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantriesViewModels
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonsViewModels
import pp.ipp.estg.dispensapessoal.pantry.ProductViewModels

fun GetFirebasePersons(pantryModel: PantryModel, personsViewModels: PersonsViewModels) {
    personsViewModels.getFirebaseMembersByPantry(pantryModel) { result ->
        result?.let {
            it.forEach { person ->
                personsViewModels.insertPerson(person)
            }
            Log.d("Received FIREBASE Products", it.toString())
        } ?: run {
            Log.d("Error", "Failed to retrieve products")
        }
    }

}

fun GetFirebaseProducts(pantryModel: PantryModel, productViewModels: ProductViewModels) {

    productViewModels.getFirebaseProductsByPantry(pantryModel) { result ->
        result?.let {
            it.forEach { product ->
                productViewModels.insertProduct(product)
            }
            Log.d("Received FIREBASE Products", it.toString())
        } ?: run {
            Log.d("Error", "Failed to retrieve products")
        }
    }

}

@Composable
fun GetFirebasePantries(person: PersonModel?) {
    val pantriesViewModels: PantriesViewModels = viewModel()
    val productViewModels: ProductViewModels = viewModel()
    val personsViewModels: PersonsViewModels = viewModel()
    //var pantriesList: List<PantryModel> = emptyList()

    if (person != null) {
        pantriesViewModels.getFirebasePantriesByPerson(person = person) { result ->
            result?.let {
                it.forEach { pantry ->
                    pantriesViewModels.insertPantry(pantry)
                    GetFirebaseProducts(pantryModel = pantry, productViewModels)
                    GetFirebasePersons(pantryModel = pantry, personsViewModels)
                }
            } ?: run {
                // Handle the case where the callback returns null (e.g., an error occurred)
                Log.d("Error", "Failed to retrieve pantries")
            }
        }
    }

}

@Composable
fun Buttonsignup(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.regista_te)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = { navController.navigate("signup") },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Color.Companion.Blue
            )
        )
    }
}

@Preview
@Composable
fun PreviewLoginPage() {
    LoginPage(navController = rememberNavController())
}

@Composable
fun LoginPage(navController: NavHostController) {
    var person by remember { mutableStateOf<PersonModel?>(null) }
    val personsViewModels: PersonsViewModels = viewModel()

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.pantry),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        var email by remember { mutableStateOf("") }
        var pin by remember { mutableStateOf("") }

        Text(text = "Login", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Email") },
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(20.dp))
        var passwordVisibility by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Password") },
            value = pin,
            onValueChange = { pin = it },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        var login by remember { mutableStateOf(false) }
        var alert by remember { mutableStateOf(false) }
        var alertText by remember { mutableStateOf("") }

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    if (pin.length < 6) {
                        alertText = "Formato da password incorreto...\n(ex.: 123456)"
                        alert = true
                    } else if (!isValidEmail(email)){
                        alertText = "Formato do email incorreto...\n(ex.: exemplo@gmail.com)"
                        alert = true
                    } else {
                        login = true
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("Forgot password?"),
            onClick = { navController.navigate("forgotpassw") },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default
            )
        )

        Spacer(modifier = Modifier.height(10.dp))
        Buttonsignup(navController)

        if(alert){
            AlertDialog(
                onDismissRequest = {
                    alert = false
                },
                title = { Text("AVISO") },
                text = {
                    Text(alertText)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            alert = false
                            navController.navigate("login")
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        var getData by remember { mutableStateOf(false) }
        if (login) {
            LaunchedEffect(key1 = Unit) {
                personsViewModels.getFirebasePerson(email = email) { fetchedPerson ->
                    if (fetchedPerson != null) {
                        person = fetchedPerson
                        fetchedPerson.telem.let { telem ->
                            personsViewModels.insertPerson(fetchedPerson)
                            getData = true
                            personsViewModels.login(navController, email, pin, telem)
                        }
                        login = false
                    } else {
                        alert = true
                        alertText = "Utilizador nao encontrado...\nTente novamente"
                    }
                }
            }
        }

        if (getData && person != null) {
            GetFirebasePantries(person = person)
            Log.d("GET FIREBASE DATA", "COMPLETED")
            getData = false
        }

    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    return emailRegex.matches(email)
}

@Preview
@Composable
fun PreviewSignupPage() {
    SignupPage(navController = rememberNavController())
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPage(navController: NavHostController) {
    val personsViewModels: PersonsViewModels = viewModel()
    var validate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Login"
                        )
                    }
                }
            )
        }
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.pantry),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Ajuste a altura conforme necessário
            )

            var phoneNumber by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var username by remember { mutableStateOf("") }
            var firstName by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }

            Text(
                text = "Signup",
                style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
            )

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "First Name") },
                value = firstName,
                onValueChange = { firstName = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Last Name") },
                value = lastName,
                onValueChange = { lastName = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Username") },
                value = username,
                onValueChange = { username = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Email") },
                value = email,
                onValueChange = { email = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Nº Telemóvel") },
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))
            var passwordVisibility by remember { mutableStateOf(false) }

            TextField(
                label = { Text(text = "Password") },
                value = password,
                onValueChange = { password = it },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisibility = !passwordVisibility },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    validate = true
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Criar Conta")
            }

            var error by remember { mutableStateOf(false) }
            var alertText by remember { mutableStateOf("") }
            if(validate){

                if(phoneNumber.length != 9 || !phoneNumber.isDigitsOnly()){
                    error = true
                    alertText = "Formato Nº telemóvel incorreto...\n(ex.:999111222)"
                } else if(password.length < 6 || !password.isDigitsOnly()){
                    error = true
                    alertText = "Formato Password incorreto...\n(ex.:123456)"
                } else if(username.isBlank()){
                    error = true
                    alertText = "Formato Username incorreto...\n(ex.:username123)"
                } else if(firstName.isBlank()){
                    error = true
                    alertText = "Formato Primeiro Nome incorreto...\n(ex.:João)"
                } else if(lastName.isBlank()){
                    error = true
                    alertText = "Formato Último Nome incorreto...\n(ex.:Texeira)"
                } else if(email.isBlank() || !isValidEmail(email)){
                    error = true
                    alertText = "Formato Email incorreto...\n(ex.:user@gmail.com)"
                } else {
                    val personModel = PersonModel(
                        phoneNumber.toInt(),
                        password,
                        username,
                        firstName,
                        lastName,
                        email,
                        emptyList()
                    )

                    // Envia e-mail e senha para o Firebase Authentication
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                personsViewModels.insertPerson(personModel)
                                navController.navigate("login")
                            } else {
                                // Handle errors no registro no Firebase Authentication
                                Log.d("TAG FIREBASE AUTH FAIL", "ERRO")
                            }
                        }
                }

            }

            if(error){
                AlertDialog(
                    onDismissRequest = {
                        error = false
                    },
                    title = { Text("AVISO") },
                    text = {
                        Text(alertText)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                error = false
                                navController.navigate("signup")
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
                error = false
            }
        }
    }
}

@Preview
@Composable
fun Previewforgotpassword() {
    forgotpassword(navController = rememberNavController())
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun forgotpassword(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Login"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = { },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Reset Password")
            }
        }
    }
}