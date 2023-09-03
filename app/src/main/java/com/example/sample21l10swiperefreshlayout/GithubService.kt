package com.example.sample21l10swiperefreshlayout

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object GithubService {

    private const val BASE_URL = "https://api.github.com/"
    val api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create<GithubInterface>()
    }
}