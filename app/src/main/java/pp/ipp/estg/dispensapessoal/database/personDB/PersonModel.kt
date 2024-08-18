package pp.ipp.estg.dispensapessoal.database.personDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonModel(
    @PrimaryKey
    val telem: Int,
    val password: String,
    val username: String,
    val firstname: String,
    val lastName: String,
    val email: String,
    val pantries: List<Int>?
){
    @Suppress("unused")
    constructor() : this(0, "", "", "", "", "", emptyList())
}