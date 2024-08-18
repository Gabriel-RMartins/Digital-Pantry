package pp.ipp.estg.dispensapessoal.database.pantryDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PantryDao {

    @Query("select * from PantryModel")
    fun getPantries(): LiveData<List<PantryModel>>

    @Query("SELECT * FROM PantryModel WHERE pantry_id IN (SELECT pantries FROM PersonModel WHERE telem = :member_telem)")
    fun getPantriesByMember(member_telem: Int): LiveData<List<PantryModel>>

    @Query("select * from PantryModel where pantry_id = :pantry_id")
    fun getOnePantry(pantry_id:Int): LiveData<PantryModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pantryModel: PantryModel)

    @Update
    suspend fun update(pantryModel: PantryModel)

    @Delete
    suspend fun delete(pantryModel: PantryModel)

}