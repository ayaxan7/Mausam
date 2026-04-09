package com.ayaan.mausam.data.api

import com.google.gson.annotations.SerializedName

data class NominatimPlaceDto(
	val lat: String,
	val lon: String,
	val name: String?,
	@SerializedName("display_name") val displayName: String,
	val address: NominatimAddressDto?
)

data class NominatimAddressDto(
	val city: String?,
	val town: String?,
	val village: String?,
	val municipality: String?,
	val county: String?,
	val state: String?,
	val country: String?
)

