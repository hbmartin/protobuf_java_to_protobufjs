/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufdemo

import com.company.generated.model.services.roomservice.GetRoomDetailsResponseModel
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.retrofit2protobuf.SendProtobufToFlipperFromRetrofit
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import tutorial.Dataformat

class ApiClient {
    private val baseUrl = "http://${BuildConfig.DesktopIP}/"

    val service = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addNetworkInterceptor(FlipperOkhttpInterceptor(PluginProvider.networkFlipperPlugin))
                .build()
        )
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(ProtoConverterFactory.create())
        .build()
        .run {
            create(PersonService::class.java)
        }

    init {
        SendProtobufToFlipperFromRetrofit(baseUrl, PersonService::class.java)
    }
}

interface PersonService {
    @GET("person.proto")
    fun getPerson(): Single<Dataformat.Person>

    @POST("person.proto")
    fun postPerson(@Body person: Dataformat.Person): Single<Dataformat.Person>

    @GET("room.proto")
    fun getRoom(): Single<GetRoomDetailsResponseModel>
}
