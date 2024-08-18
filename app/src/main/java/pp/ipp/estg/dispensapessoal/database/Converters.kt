package pp.ipp.estg.dispensapessoal.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pp.ipp.estg.dispensapessoal.database.personDB.PersonModel
import pp.ipp.estg.dispensapessoal.database.productDB.ProductModel

class Converters {

    @TypeConverter
    fun fromString(value: String?): List<Int>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(value: List<Int>?): String? {
        if (value == null) {
            return null
        }
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromProductsList(products: List<ProductModel>?): String {
        return Gson().toJson(products)
    }

    @TypeConverter
    fun toProductsList(productsString: String?): List<ProductModel> {
        val type = object : TypeToken<List<ProductModel>>() {}.type
        return Gson().fromJson(productsString, type) ?: emptyList()
    }


    @TypeConverter
    fun fromMembersList(members: List<PersonModel>?): String {
        return Gson().toJson(members)
    }

    @TypeConverter
    fun toMembersList(membersString: String?): List<PersonModel> {
        val type = object : TypeToken<List<PersonModel>>() {}.type
        return Gson().fromJson(membersString, type) ?: emptyList()
    }


    @TypeConverter
    fun fromStringList(pantries: List<String>?): String? {
        // Convert the list to a string representation, e.g., JSON
        return Gson().toJson(pantries)
    }

    @TypeConverter
    fun toStringList(pantriesString: String?): List<String>? {
        // Convert the string representation back to a list of strings
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(pantriesString, type)
    }
}
