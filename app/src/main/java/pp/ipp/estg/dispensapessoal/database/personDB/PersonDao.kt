package pp.ipp.estg.dispensapessoal.database.personDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonDao {

    @Query("select * from PersonModel")
    fun getPersons(): LiveData<List<PersonModel>>

    @Query("select * from PersonModel where telem = :telem")
    fun getOnePerson(telem: Int): LiveData<PersonModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(personModel: PersonModel)

    @Update
    suspend fun update(personModel: PersonModel)

    @Delete
    suspend fun delete(personModel: PersonModel)

}