package dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate

@Serializable
data class News(
    val id: Int,
    val title: String,
    @Serializable(with = PlaceSerializer::class) val place: Place?,
    val description: String?,
    @SerialName("site_url") val siteUrl: String?,
    @SerialName("favorites_count") val favoritesCount: Int,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("publication_date") val publicationDateTimestamp: Long,
    var rating: Double? = null
) {
    val publicationDate: LocalDate
        get() = LocalDate.ofEpochDay(publicationDateTimestamp / (24 * 60 * 60))
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Place::class)
object PlaceSerializer : KSerializer<Place> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Place", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Place) {
        encoder.encodeString(value.title)
    }

    override fun deserialize(decoder: Decoder): Place {

        return when (val jsonElement = (decoder as JsonDecoder).decodeJsonElement()) {
            is JsonObject -> Place(
                title = jsonElement["title"]?.jsonPrimitive?.content ?: "Неизвестно",
                address = jsonElement["address"]?.jsonPrimitive?.content ?: "Неизвестно"
            )
            is JsonNull -> Place(title = "Неизвестно", address = "Неизвестно")
            else -> Place(title = "Неизвестно", address = "Неизвестно")
        }
    }
}

@Serializable
data class Place(
    val title: String,
    val address: String
)