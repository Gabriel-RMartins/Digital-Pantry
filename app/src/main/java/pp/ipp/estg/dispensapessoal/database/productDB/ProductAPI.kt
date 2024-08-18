package pp.ipp.estg.dispensapessoal.pantry

import pp.ipp.estg.dispensapessoal.Retrofit.ProductDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductAPI {

    @GET("/pantries.json")
    suspend fun getProducts(@Query("name") query:String) : Response<ProductDataResponse>

}