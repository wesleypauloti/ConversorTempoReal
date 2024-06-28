
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoService {
    @GET("/api/v3/simple/price")
    fun getCurrencyRates(
        @Query("vs_currencies") vsCurrencies: String,
        @Query("ids") ids: String = "bitcoin" // Adicione um valor padrão
    ): Call<Map<String, Map<String, Double>>>
}
