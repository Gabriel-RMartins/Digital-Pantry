package pp.ipp.estg.dispensapessoal.database.pantryDB

import pp.ipp.estg.dispensapessoal.Retrofit.PantryDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PantryAPI {

    @GET("/pantries.json")
    suspend fun getPantries(
        @Query("name") query:String) : Response<PantryDataResponse>

}