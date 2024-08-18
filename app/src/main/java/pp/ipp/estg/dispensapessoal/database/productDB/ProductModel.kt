package pp.ipp.estg.dispensapessoal.database.productDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductModel(
    @PrimaryKey
    val product_id: Int,
    val name: String,
    val brand: String,
    val qtd: Float,
    val qtd_type: String
){
    @Suppress("unused")
    constructor() : this(0, "", "", 0F, "")
}