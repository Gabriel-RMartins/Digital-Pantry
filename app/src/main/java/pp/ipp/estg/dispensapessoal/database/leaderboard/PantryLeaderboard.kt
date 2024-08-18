package pp.ipp.estg.dispensapessoal.database.leaderboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PantryLeaderboard(
    @PrimaryKey
    val pos: Int,
    val idPantry: Int,
    val numProducts: Int
){
    @Suppress("unused")
    constructor() : this(0, 0, 0)
}

