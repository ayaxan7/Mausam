package com.ayaan.mausam.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {

    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String = "jsonv2",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 8
    ): Response<List<NominatimPlaceDto>>
}

