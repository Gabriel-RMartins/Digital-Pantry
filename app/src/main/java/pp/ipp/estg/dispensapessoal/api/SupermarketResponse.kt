package pp.ipp.estg.dispensapessoal.api

data class Location(
    val type: String,
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

data class Properties(
    val name: String?,
    val country: String,
    val country_code: String,
    val county: String,
    val city: String,
    val postcode: String,
    val district: String,
    val suburb: String,
    val street: String,
    val lon: Double,
    val lat: Double,
    val formatted: String,
    val address_line1: String,
    val address_line2: String,
    val categories: List<String>,
    val details: List<String>,
    val datasource: Datasource,
    val distance: Int,
    val place_id: String
)

data class Datasource(
    val sourcename: String,
    val attribution: String,
    val license: String,
    val url: String,
    val raw: Raw
)

data class Raw(
    val shop: String?,
    val osm_id: Long,
    val building: String?,
    val osm_type: String?
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)
