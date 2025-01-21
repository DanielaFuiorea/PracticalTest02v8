package ro.pub.cs.systems.eim.practicaltest02v8.model

data class BitcoinResponse(
    val time: Time,
    val disclaimer: String,
    val bpi: Bpi
)

data class Time(
    val updated: String,
    val updatedISO: String,
    val updateduk: String
)

data class Bpi(
    val USD: Currency,
    val EUR: Currency
)

data class Currency(
    val code: String,
    val rate: String,
    val description: String,
    val rate_float: Double
)
