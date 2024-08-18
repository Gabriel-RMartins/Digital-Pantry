package pp.ipp.estg.dispensapessoal.database.personDB

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel

data class PersonRepository(
    val personDao: PersonDao,
    //val restAPI: PersonAPI
) {
    fun getPersons(): LiveData<List<PersonModel>> {
        return personDao.getPersons()
    }

    /**suspend fun updatePantriesOnline(): Response<PersonDataResponse> {
        return this.restAPI.getPantries("teste")
    }*/

    fun getPerson(telem:Int): LiveData<PersonModel> {
        return personDao.getOnePerson(telem)
    }

    suspend fun insert(personModel: PersonModel){
        personDao.insert(personModel)
    }

    suspend fun insertPantryToPerson(person: PersonModel, pantry: PantryModel) {
        withContext(Dispatchers.IO) {
            val updatedPantries = person.pantries.orEmpty().toMutableList()
            updatedPantries.add(pantry.pantry_id)
            personDao.update(person.copy(pantries = updatedPantries))
        }
    }

    suspend fun update(personModel: PersonModel){
        personDao.update(personModel)
    }

    suspend fun delete(personModel: PersonModel){
        personDao.delete(personModel)
    }

    suspend fun deletePantryfromPerson(person: PersonModel, pantry: PantryModel){
        withContext(Dispatchers.IO) {
            val pantries = person.pantries.orEmpty().toMutableList()
            pantries.removeAll { it == pantry.pantry_id }
            personDao.update(person.copy(pantries = pantries))
        }
    }
}