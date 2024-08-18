package pp.ipp.estg.dispensapessoal.database.pantryDB

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pp.ipp.estg.dispensapessoal.database.ProjectDatabase
import pp.ipp.estg.dispensapessoal.database.leaderboard.PantryLeaderboard
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

class PantriesViewModels(application: Application) : AndroidViewModel(application) {

    val repository: PantryRepository

    val db = Firebase.firestore

    init {
        val db = ProjectDatabase.getDatabase(application)
        repository = PantryRepository(db.getPantryDao())
    }

    fun getallPantries(): LiveData<List<PantryModel>> {
        return repository.getPantries()
    }

    fun getFirebasePantriesCount(callback: (Int) -> Unit) {
        db.collection("pantry")
            .get()
            .addOnSuccessListener { result ->
                val count = result.size()
                callback(count)
            }
            .addOnFailureListener { exception ->
                Log.d("FIREBASE PANTRIES COUNT FAIL", exception.toString())
                callback(0)
            }
    }

    fun getFirebasePantry(idPantry: Int, callback: (PantryModel?) -> Unit){
        val docRef = db.collection("pantry").document(idPantry.toString())
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val pantry = documentSnapshot.toObject<PantryModel>()
                Log.d("GET PANTRY FIREBASE SUCCESS", documentSnapshot.toString())
                callback(pantry)
            }
            .addOnFailureListener { exception ->
                Log.d("GET PANTRY FIREBASE FAIL", exception.toString())
                callback(null)
            }
    }

    fun getFirebasePantries(callback: (List<PantryModel>?) -> Unit) {
        val pantries = mutableStateListOf<PantryModel>()

        db.collection("pantry")
            .get()
            .addOnSuccessListener { result ->
                pantries.clear()
                for (document in result) {
                    val pantry_id = document["pantry_id"] as Long
                    val name = document["name"] as String
                    val products = document["products"] as List<ProductModel>?
                    val members = document["members"] as List<PersonModel>?

                    val tempPantry = PantryModel(
                        pantry_id.toInt(),
                        name,
                        products,
                        members
                    )

                    pantries.add(tempPantry)
                }
                callback(pantries)
            }
            .addOnFailureListener { exception ->
                Log.d("FIREBASE PANTRIES FAIL", exception.toString())
                callback(null)
            }
    }

    fun getFirebasePantriesByPerson(person: PersonModel, callback: (List<PantryModel>?) -> Unit) {
        val pantriesList: MutableList<PantryModel> = mutableListOf()
        person.pantries?.forEach { pantryId ->
            val docRef = db.collection("pantry").document(pantryId.toString())
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("FIREBASE PANTRY FAIL", e.toString())
                    callback(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val pantry = snapshot.toObject<PantryModel>()
                    if (pantry != null) {
                        pantriesList.add(pantry)
                    }
                } else {
                    Log.d("Pantry by Person FIREBASE document", "empty")
                }

                if (pantriesList.size == person.pantries.size) {
                    callback(pantriesList)
                }
            }
        }
    }

    fun insertPantry(pantryModel: PantryModel){
        db.collection("pantry").document(pantryModel.pantry_id.toString())
            .set(pantryModel)
            .addOnSuccessListener {
                Log.d("FIRESTORE INSERT PANTRY SUCCESS", pantryModel.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.insert(pantryModel)
                }

            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT PANTRY FAILURE", it.toString())
            }
    }

    fun deletePantry(pantry: PantryModel){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(pantry)
        }
    }

    fun getOnePantry(id:Int): LiveData<PantryModel> {
        return repository.getPantry(id)
    }

    fun addMemberToPantry(pantry: PantryModel, person: PersonModel) {
        val updatedMembers = pantry.members.orEmpty().toMutableList()
        updatedMembers.add(person)
        val tempPantry = pantry.copy(members = updatedMembers)

        db.collection("pantry").document(pantry.pantry_id.toString())
            .set(tempPantry)
            .addOnSuccessListener {
                Log.d("FIRESTORE INSERT MEMBER SUCCESS", person.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPantry)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT MEMBER FAILURE", it.toString())
            }
    }

    fun addProductToPantry(pantry: PantryModel, product: ProductModel) {
        val updatedProducts = pantry.products.orEmpty().toMutableList()
        updatedProducts.add(product)
        val tempPantry = pantry.copy(products = updatedProducts)

        db.collection("pantry").document(pantry.pantry_id.toString())
            .set(tempPantry)
            .addOnSuccessListener {
                Log.d("FIRESTORE INSERT MEMBER SUCCESS", product.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPantry)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT MEMBER FAILURE", it.toString())
            }
    }

    fun deleteMemberFromPantry(pantry: PantryModel, person: PersonModel){
        val updatedMembers = pantry.members.orEmpty().toMutableList()
        updatedMembers.removeAll { it.telem == person.telem }
        val tempPantry = pantry.copy(members = updatedMembers)

        db.collection("pantry").document(pantry.pantry_id.toString())
            .set(tempPantry)
            .addOnSuccessListener {
                Log.d("FIRESTORE UPDATE PANTRY SUCCESS", person.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPantry)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT MEMBER FAILURE", it.toString())
            }
    }

    fun deleteProductFromPantry(pantry: PantryModel, product: ProductModel){
        val updatedProducts = pantry.products.orEmpty().toMutableList()
        updatedProducts.removeAll { it.product_id == product.product_id }
        val tempPantry = pantry.copy(products = updatedProducts)

        db.collection("pantry").document(pantry.pantry_id.toString())
            .set(tempPantry)
            .addOnSuccessListener {
                Log.d("FIRESTORE UPDATE PANTRY SUCCESS", product.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPantry)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT MEMBER FAILURE", it.toString())
            }
    }

    fun updateProductFromPantry(pantry: PantryModel, product: ProductModel) {
        val updatedProducts = pantry.products.orEmpty().toMutableList()
        updatedProducts.removeAll { it.product_id == product.product_id }
        updatedProducts.add(product)
        val tempPantry = pantry.copy(products = updatedProducts)

        db.collection("pantry").document(pantry.pantry_id.toString())
            .set(tempPantry)
            .addOnSuccessListener {
                Log.d("FIRESTORE UPDATE MEMBER SUCCESS", product.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(tempPantry)
                }
            }
            .addOnFailureListener {
                Log.d("FIRESTORE UPDATE MEMBER FAILURE", it.toString())
            }
    }

}