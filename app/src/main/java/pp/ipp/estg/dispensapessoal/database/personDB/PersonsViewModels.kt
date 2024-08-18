package pp.ipp.estg.dispensapessoal.database.personDB

import android.app.Application
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pp.ipp.estg.dispensapessoal.Retrofit.RetrofitHelper
import pp.ipp.estg.dispensapessoal.database.ProjectDatabase
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

class PersonsViewModels(application: Application) : AndroidViewModel(application) {

    val repository: PersonRepository
    val authState : MutableLiveData<AuthStatus>
    val fAuth : FirebaseAuth
    val db = Firebase.firestore

    init {
        val db = ProjectDatabase.getDatabase(application)
        //val restAPI = RetrofitHelper.getInstance().create(PersonAPI::class.java)
        repository = PersonRepository(db.getPersonDao())
        authState = MutableLiveData(AuthStatus.NOLOGGIN)
        fAuth = Firebase.auth
    }

    fun register(email:String, password:String){
        viewModelScope.launch {
            try{
                val result = fAuth.createUserWithEmailAndPassword(email, password).await()
                if (result != null && result.user != null){
                    authState.postValue(AuthStatus.LOGGED)
                    Log.d("Register","logged in")
                    return@launch
                }
                Log.d("Register","anonymous")
                authState.postValue(AuthStatus.NOLOGGIN)
                return@launch
            } catch( e:Exception) {}
        }
    }

    fun login(navController: NavController, email:String, password:String, telem: Int){
        viewModelScope.launch {
            try{
                val result = fAuth.signInWithEmailAndPassword(email, password).await()
                Log.d("FIREBASE LOGIN STATUS", result.toString())
                if (result != null && result.user != null){
                    authState.postValue(AuthStatus.LOGGED)
                    Log.d("FIREBASE Login","logged in")
                    navController.navigate("Página Principal/$telem")
                    return@launch
                }
                Log.d("Login","anonymous")
                authState.postValue(AuthStatus.NOLOGGIN)
                return@launch
            } catch( e:Exception) {
                Log.d("FIREBASE FAILED", e.toString())
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            fAuth.signOut()
            authState.postValue(AuthStatus.NOLOGGIN)
            Log.d("Login","logout")
        }
    }

    enum class AuthStatus {
        LOGGED, NOLOGGIN
    }

    fun getallPersons(): LiveData<List<PersonModel>> {
        return repository.getPersons()
    }

    fun getFirebasePerson(email: String, callback: (PersonModel?) -> Unit) {
        val docRef = db.collection("person").document(email)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("FIREBASE LOGIN FAIL", e.toString())
                callback(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val person = snapshot.toObject<PersonModel>()
                Log.d("Person Firebase document", person.toString())
                callback(person)
            } else {
                Log.d("Person Firebase document", "empty")
                callback(null)
            }
        }
    }

    fun getFirebaseMembersByPantry(pantry: PantryModel, callback: (List<PersonModel>?) -> Unit) {
        val personList: MutableList<PersonModel> = mutableListOf()

        pantry.members?.forEach { memberId ->
            val docRef = db.collection("person").document(memberId.toString())

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("FIREBASE PERSONS FAIL", e.toString())
                    callback(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val person = snapshot.toObject<PersonModel>()
                    if (person != null) {
                        personList.add(person)
                        Log.d("Member by Pantry Firebase document", person.toString())
                    }
                } else {
                    Log.d("Member by Pantry Firebase document", "empty")
                }

                if (personList.size == pantry.members.size) {
                    callback(personList)
                }
            }
        }
    }

    fun insertPerson(person: PersonModel) {

        db.collection("person").document(person.email)
            .set(person)
            .addOnSuccessListener {
                Log.d("FIRESTORE LOGIN SUCCESS", person.toString())
                viewModelScope.launch(Dispatchers.IO){
                    repository.insert(person)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE LOGIN FAILURE", it.toString())
            }
    }

    fun insertPantryToPerson(person: PersonModel, pantry: PantryModel) {
        val updatedPantries = person.pantries.orEmpty().toMutableList()
        updatedPantries.add(pantry.pantry_id)
        val tempPerson = person.copy(pantries = updatedPantries)

        db.collection("person").document(person.email)
            .set(tempPerson)
            .addOnSuccessListener {
                Log.d("FIRESTORE INSERT PANTRY SUCCESS", person.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPerson)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT PANTRY FAILURE", it.toString())
            }
    }

    fun updatePerson(person: PersonModel) {
        db.collection("person").document(person.email)
            .set(person)
            .addOnSuccessListener {
                Log.d("FIRESTORE UPDATE PERSON SUCCESS", person.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(person)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE UPDATE PERSON FAILURE", it.toString())
            }
    }

    fun deletePerson(person: PersonModel){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(person)
        }
    }

    fun deletePantryfromPerson(person: PersonModel, pantry: PantryModel) {
        val pantries = person.pantries.orEmpty().toMutableList()
        pantries.removeAll { it == pantry.pantry_id }
        val tempPerson = person.copy(pantries = pantries)

        db.collection("person").document(person.email)
            .set(tempPerson)
            .addOnSuccessListener {
                Log.d("FIRESTORE DELETE PANTRY SUCCESS", person.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPerson)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE DELETE PANTRY FAILURE", it.toString())
            }
    }

    fun getOnePerson(telem:Int): LiveData<PersonModel> {
        return repository.getPerson(telem)
    }

}