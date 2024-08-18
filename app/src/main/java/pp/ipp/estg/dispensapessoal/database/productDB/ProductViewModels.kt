package pp.ipp.estg.dispensapessoal.pantry

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pp.ipp.estg.dispensapessoal.database.ProjectDatabase
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

class ProductViewModels(application: Application) : AndroidViewModel(application) {

    val repository: ProductRepository
    val db = Firebase.firestore

    init {
        val db = ProjectDatabase.getDatabase(application)
        repository = ProductRepository(db.getProductDao())
    }

    fun getallProducts(): LiveData<List<ProductModel>> {
        return repository.getProducts()
    }

    fun getFirebaseProductsByPantry(pantry: PantryModel, callback: (List<ProductModel>?) -> Unit) {
        val productsList: MutableList<ProductModel> = mutableListOf()

        pantry.products?.forEach { product ->
            val docRef = db.collection("product").document(product.product_id.toString())

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("FIREBASE PRODUCTS FAIL", e.toString())
                    callback(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val product = snapshot.toObject<ProductModel>()
                    if (product != null) {
                        productsList.add(product)
                        Log.d("Product by Pantry Firebase document", product.toString())
                    }
                } else {
                    Log.d("Product by Pantry Firebase document", "empty")
                }

                // Check if all products have been processed
                if (productsList.size == pantry.products.size) {
                    callback(productsList)
                }
            }
        }
    }

    fun insertProduct(productModel: ProductModel) {
        db.collection("product").document(productModel.product_id.toString())
            .set(productModel)
            .addOnSuccessListener {
                Log.d("FIRESTORE INSERT PRODUCT SUCCESS", productModel.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.insert(productModel)
                }

            }
            .addOnFailureListener {
                Log.d("FIRESTORE INSERT PRODUCT FAILURE", it.toString())
            }
    }

    fun updatetProduct(productModel: ProductModel) {
        db.collection("product").document(productModel.product_id.toString())
            .set(productModel)
            .addOnSuccessListener {
                Log.d("FIRESTORE UPDATE PRODUCT SUCCESS", productModel.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(productModel)
                }

            }
            .addOnFailureListener {
                Log.d("FIRESTORE UPDATE PRODUCT FAILURE", it.toString())
            }
    }

    fun deleteProductFirebase(productModel: ProductModel){
        db.collection("product").document(productModel.product_id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("FIRESTORE DELETE PRODUCT SUCCESS", productModel.toString())

                viewModelScope.launch(Dispatchers.IO) {
                    repository.delete(productModel)
                }

            }
            .addOnFailureListener {
                Log.d("FIRESTORE DELETE PRODUCT FAILURE", it.toString())
            }
    }

    fun deleteProduct(productModel: ProductModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(productModel)
        }
    }

    fun getOneProduct(id: Int): LiveData<ProductModel> {
        return repository.getProduct(id)
    }

}