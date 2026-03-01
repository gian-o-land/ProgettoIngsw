package DTO.source

data class PhonePrefix(val displayName: String, val prefix: String, val flagEmoji: String)

val CommonPhonePrefixes = listOf(
    PhonePrefix("(+39)", "+39", "🇮🇹"),
    PhonePrefix("(+44)", "+44", "🇬🇧"),
    PhonePrefix("(+1)", "+1", "🇺🇸"),
    PhonePrefix("(+49)", "+49", "🇩🇪"),
    PhonePrefix("(+33)", "+33", "🇫🇷"),
    PhonePrefix("(+34)", "+34", "🇪🇸"),
    // Aggiungi altri prefissi comuni o una lista più completa
)
val DefaultPhonePrefix = CommonPhonePrefixes.first { it.prefix == "+39" } // Italia come default