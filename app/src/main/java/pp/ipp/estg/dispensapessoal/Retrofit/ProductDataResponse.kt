package pp.ipp.estg.dispensapessoal.Retrofit

import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

data class ProductDataResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<ProductModel>

)