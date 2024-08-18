package pp.ipp.estg.dispensapessoal.Retrofit

import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel

data class PersonDataResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<PersonModel>
)
