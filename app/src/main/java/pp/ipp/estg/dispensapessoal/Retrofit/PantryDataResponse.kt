package pp.ipp.estg.dispensapessoal.Retrofit

import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel

data class PantryDataResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<PantryModel>
)
