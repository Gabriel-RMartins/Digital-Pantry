package pp.ipp.estg.dispensapessoal.database.pantryDB

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pp.ipp.estg.dispensapessoal.Retrofit.PantryDataResponse
import pp.ipp.estg.dispensapessoal.database.personDB.PersonDao
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel
import retrofit2.Response

data class PantryRepository(
    val pantryDao: PantryDao,
    //val restAPI: PantryAPI
) {
    fun getPantries(): LiveData<List<PantryModel>> {
        return pantryDao.getPantries()
    }

    /**suspend fun updatePantriesOnline(): Response<PantryDataResponse> {
    return this.restAPI.getPantries("teste")
    }*/

    fun getPantry(id: Int): LiveData<PantryModel> {
        return pantryDao.getOnePantry(id)
    }

    suspend fun insert(pantryModel: PantryModel) {
        pantryDao.insert(pantryModel)
    }

    suspend fun addMemberToPantry(pantry: PantryModel, person: PersonModel) {
        withContext(Dispatchers.IO) {
            val updatedMembers = pantry.members.orEmpty().toMutableList()
            updatedMembers.add(person)
            pantryDao.update(pantry.copy(members = updatedMembers))
        }
    }

    suspend fun addProductToPantry(pantry: PantryModel, product: ProductModel) {
        withContext(Dispatchers.IO) {
            val updatedProducts = pantry.products.orEmpty().toMutableList()
            updatedProducts.add(product)
            pantryDao.update(pantry.copy(products = updatedProducts))
        }
    }


    suspend fun update(pantryModel: PantryModel) {
        pantryDao.update(pantryModel)
    }

    suspend fun delete(pantryModel: PantryModel) {
        pantryDao.delete(pantryModel)
    }
}
