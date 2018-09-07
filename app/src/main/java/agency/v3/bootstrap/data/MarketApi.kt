package agency.v3.bootstrap.data

import io.reactivex.Observable
import retrofit2.http.GET

interface MarketApi {

    @GET("/1.0/ref-data/symbols")
    fun symbols(): Observable<List<TradingSymbol>>
}

data class TradingSymbol(
        val symbol: String,
        val name: String,
        val date: String,
        val isEnabled: Boolean,
        val type: String,
        val iexId: String
)