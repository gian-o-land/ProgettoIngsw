package gui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getIconaEColore(condizione: CondizioneMeteo): Pair<ImageVector, Color> {
    return when (condizione) {
        CondizioneMeteo.SOLE -> Pair(Icons.Default.WbSunny, Color(0xFFFDB813))
        CondizioneMeteo.NUVOLO -> Pair(Icons.Default.Cloud, Color.Gray)
        CondizioneMeteo.PIOGGIA -> Pair(Icons.Default.BeachAccess, Color(0xFF4A90E2))
        CondizioneMeteo.NEVE -> Pair(Icons.Default.AcUnit, Color.Cyan)
        CondizioneMeteo.SCONOSCIUTO -> Pair(Icons.Default.Cloud, Color.LightGray)
    }
}

@Composable
fun usaMeteo(
    latitude: Double,
    longitude: Double,
    giornoSelezionatoMillis: Long?,
    fornitore: FornitoreMeteo
): CondizioneMeteo? {
    var mappaMeteo by remember { mutableStateOf<Map<String, CondizioneMeteo>>(emptyMap()) }

    LaunchedEffect(latitude, longitude) {
        val mappaOttenuta = withContext(Dispatchers.IO) {
            fornitore.ottieniPrevisioni(latitude, longitude)
        }
        mappaMeteo = mappaOttenuta
    }

    return remember(giornoSelezionatoMillis, mappaMeteo) {
        if (giornoSelezionatoMillis != null && mappaMeteo.isNotEmpty()) {
            val dataFormatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(giornoSelezionatoMillis))
            mappaMeteo[dataFormatoApi]
        } else {
            null
        }
    }
}



enum class CondizioneMeteo {
    SOLE, NUVOLO, PIOGGIA, NEVE, SCONOSCIUTO
}

interface FornitoreMeteo {
    suspend fun ottieniPrevisioni(latitudine: Double, longitudine: Double): Map<String, CondizioneMeteo>
}

class OpenMeteoAdapter : FornitoreMeteo {
    override suspend fun ottieniPrevisioni(latitudine: Double, longitudine: Double): Map<String, CondizioneMeteo> {
        val mappa = mutableMapOf<String, CondizioneMeteo>()
        try {
            val risposta = RetrofitClient.api.getPrevisioni(latitude = latitudine, longitude = longitudine)
            for (i in risposta.daily.time.indices) {
                val data = risposta.daily.time[i]
                val codiceOpenMeteo = risposta.daily.weathercode[i]

                // Traduce il codice Open-Meteo nel TUO standard
                mappa[data] = when (codiceOpenMeteo) {
                    0 -> CondizioneMeteo.SOLE
                    1, 2, 3, 45, 48 -> CondizioneMeteo.NUVOLO
                    in 51..67, in 80..82, in 95..99 -> CondizioneMeteo.PIOGGIA
                    in 71..77, in 85..86 -> CondizioneMeteo.NEVE
                    else -> CondizioneMeteo.SCONOSCIUTO
                }
            }
        } catch (e: Exception) {
            // Se fallisce, ritorniamo la mappa vuota (nessun meteo mostrato)
        }
        return mappa
    }
}

// Strutture specifiche di Open-Meteo (usate solo dall'Adapter)
data class MeteoResponse(val daily: DailyData)
data class DailyData(val time: List<String>, val weathercode: List<Int>)

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getPrevisioni(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "weathercode",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 16
    ): MeteoResponse
}

object RetrofitClient {
    val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}
