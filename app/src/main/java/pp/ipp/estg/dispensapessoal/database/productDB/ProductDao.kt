package pp.ipp.estg.dispensapessoal.database.productDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Query("select * from ProductModel")
    fun getProducts(): LiveData<List<ProductModel>>

    @Query("select * from ProductModel where product_id = :id")
    fun getOneProduct(id: Int): LiveData<ProductModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productModel: ProductModel)

    @Update
    suspend fun update(productModel: ProductModel)

    @Delete
    suspend fun delete(productModel: ProductModel)

}