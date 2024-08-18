package pp.ipp.estg.dispensapessoal.database.personDB

import pp.ipp.estg.dispensapessoal.Retrofit.PersonDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PersonAPI {

    @GET("/pantries.json")
    suspend fun getPantries(
        @Query("name") query:String) : Response<PersonDataResponse>

}