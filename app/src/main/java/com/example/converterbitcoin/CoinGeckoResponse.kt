data class CoinGeckoResponse(
    val market_data: MarketData
)

data class MarketData(
    val current_price: CurrentPrice
)

data class CurrentPrice(
    val brl: Double,
    val usd: Double,
    val eur: Double
)
