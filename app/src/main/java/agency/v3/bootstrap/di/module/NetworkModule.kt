package agency.v3.bootstrap.di.module

import agency.v3.bootstrap.BuildConfig
import agency.v3.bootstrap.data.MarketApi
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object {
        const val CONNECT_TIMEOUT_IN_MS = 30000
    }


    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor {
            Timber.i(it)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return okhttp3.OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_IN_MS.toLong(), TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Singleton
    @Provides
    internal fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

    @Singleton
    @Provides
    internal fun marketApi(retrofit: Retrofit): MarketApi {
        return retrofit.create(MarketApi::class.java)
    }

}