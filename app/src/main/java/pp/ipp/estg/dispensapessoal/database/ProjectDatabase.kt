package pp.ipp.estg.dispensapessoal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryDao
import pp.ipp.estg.dispensapessoal.database.pantryDB.PantryModel
import pp.ipp.estg.dispensapessoal.database.personDB.PersonDao
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductDao
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

@Database(entities = [PantryModel::class, PersonModel::class, ProductModel::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ProjectDatabase : RoomDatabase() {

    abstract fun getPantryDao(): PantryDao
    abstract fun getPersonDao(): PersonDao
    abstract fun getProductDao(): ProductDao

    companion object {
        private var INSTANCE: ProjectDatabase? = null

        fun getDatabase(context: Context): ProjectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ProjectDatabase::class.java,
                    "project-database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}