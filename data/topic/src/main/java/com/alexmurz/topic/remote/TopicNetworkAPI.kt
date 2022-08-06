package com.alexmurz.topic.remote

import retrofit2.http.*

interface TopicNetworkAPI {
    @GET("/topic/v1/newest")
    suspend fun getNewest(
        @Query("q") quantity: Int
    ): List<TopicNetworkV1DTO>

    @GET("/topic/v1/newer")
    suspend fun getNewer(
        @Query("q") quantity: Int,
        @Query("d") date: Long,
    ): List<TopicNetworkV1DTO>

    @GET("/topic/v1/older")
    suspend fun getOlder(
        @Query("q") quantity: Int,
        @Query("d") date: Long,
    ): List<TopicNetworkV1DTO>

    @Multipart
    @POST("/topic/v1/create")
    suspend fun create(
        @Part("title") title: String,
        @Part("message") message: String,
    ): TopicNetworkV1DTO
}
