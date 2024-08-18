package pp.ipp.estg.dispensapessoal.database.leaderboard

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel

class LeaderboardViewModels(application: Application) : AndroidViewModel(application) {

    val db = Firebase.firestore
    fun getFirebaseLeaderboard(callback: (List<PantryLeaderboard>?) -> Unit) {
        val _pantryLeaderboardList = mutableStateListOf<PantryLeaderboard>()

        db.collection("leaderboard")
            .get()
            .addOnSuccessListener { result ->
                _pantryLeaderboardList.clear()
                for (document in result) {
                    val pos = document["pos"] as Long
                    val idPantry = document["idPantry"] as Long
                    val numProducts = document["numProducts"] as Long

                    val pantryLeaderboard = PantryLeaderboard(
                        pos = pos.toInt(),
                        idPantry = idPantry.toInt(),
                        numProducts = numProducts.toInt()
                    )

                    _pantryLeaderboardList.add(pantryLeaderboard)
                }
                callback(_pantryLeaderboardList.toList())
            }
            .addOnFailureListener { exception ->
                Log.d("FIREBASE LEADERBOARD FAIL", exception.toString())
                callback(null)
            }
    }

    fun getPantryLeaderboard(idPantry: Int, callback: (PantryLeaderboard?) -> Unit) {
        val docRef = db.collection("leaderboard").document(idPantry.toString())
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val pantryLeaderboard = documentSnapshot.toObject<PantryLeaderboard>()
                Log.d("GET PANTRY LEADERBOARD FIREBASE SUCCESS", documentSnapshot.toString())
                callback(pantryLeaderboard)
            }
            .addOnFailureListener { exception ->
                Log.d("GET PANTRY LEADERBOARD FIREBASE FAIL", exception.toString())
                callback(null)
            }
    }

    fun setPantryLeaderboard(pantryLeaderboard: PantryLeaderboard){
        db.collection("leaderboard").document(pantryLeaderboard.idPantry.toString())
            .set(pantryLeaderboard)
            .addOnFailureListener {
                Log.d("SET PANTRY LEADERBOARD FAIL", it.toString())
            }
    }

}