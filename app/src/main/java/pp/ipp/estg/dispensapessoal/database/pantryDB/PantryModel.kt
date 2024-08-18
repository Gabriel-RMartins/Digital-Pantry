package pp.ipp.estg.dispensapessoal.database.pantryDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

@Entity
data class PantryModel(
    @PrimaryKey
    val pantry_id: Int,
    val name: String,
    val products: List<ProductModel>?,
    val members: List<PersonModel>?
){
    @Suppress("unused")
    constructor() : this(0, "", emptyList(), emptyList())
}