package pp.ipp.estg.dispensapessoal.pantry

import androidx.lifecycle.LiveData
import pp.ipp.estg.dispensapessoal.Retrofit.ProductDataResponse
import pp.ipp.estg.dispensapessoal.database.productDB.ProductDao
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel
import retrofit2.Response

data class ProductRepository(
    val productDao: ProductDao,
    //val restAPI: ProductAPI
) {    fun getProducts(): LiveData<List<ProductModel>> {
        return productDao.getProducts()
    }

    /**suspend fun updateProductsOnline(): Response<ProductDataResponse> {
        return this.restAPI.getProducts("teste")
    }*/

    fun getProduct(id:Int): LiveData<ProductModel> {
        return productDao.getOneProduct(id)
    }

    suspend fun insert(productModel: ProductModel){
        productDao.insert(productModel)
    }

    suspend fun update(productModel: ProductModel){
        productDao.update(productModel)
    }

    suspend fun delete(productModel: ProductModel){
        productDao.delete(productModel)
    }
}
