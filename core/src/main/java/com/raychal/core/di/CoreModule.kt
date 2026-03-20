package com.raychal.core.di

import androidx.room.Room
import com.raychal.core.BuildConfig
import com.raychal.core.data.GameRepository
import com.raychal.core.data.source.local.room.GameDatabase
import com.raychal.core.data.source.remote.network.ApiService
import com.raychal.core.domain.repository.IGameRepository
import com.raychal.core.domain.usecase.GameInteractor
import com.raychal.core.domain.usecase.GameUseCase
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.utils.network.ConnectivityObserver
import com.raychal.core.utils.network.NetworkObserver
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    factory { get<GameDatabase>().gameDao() }
    single {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("rawg".toCharArray())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
                androidContext(),
                GameDatabase::class.java, "Game.db"
            ).fallbackToDestructiveMigration(false)
            .openHelperFactory(factory)
            .build()
    }
}

val networkModule = module {
    single<NetworkObserver> { ConnectivityObserver(androidContext()) }
    single {
        val hostname = "api.rawg.io"
        val certificatePinner = CertificatePinner.Builder()
            .add(hostname, "sha256/zq2pI6/bdpjVKNaU4/fUK1Fjb8iQAnfA9FnajywDIPU=")
            .build()
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("key", BuildConfig.API_KEY)
                    .build()
                val request = chain.request().newBuilder().url(url).build()
                chain.proceed(request)
            }
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single<IGameRepository> {
        GameRepository(get(), get())
    }
}

val useCaseModule = module {
    factory<GameUseCase> { GameInteractor(get()) }
}

val navigationModule = module {
    single { NavigationManager() }
}
