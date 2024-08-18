package pp.ipp.estg.dispensapessoal.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyApiService {
    @GET("places")
    fun getSupermarkets(
        @Query("categories") categories: String,
        @Query("conditions") named: String,
        @Query("filter") filter: String,
        @Query("bias") bias: String,
        @Query("limit") limit: Int,
        @Query("apiKey") apiKey: String
    ): Call<Location>
}