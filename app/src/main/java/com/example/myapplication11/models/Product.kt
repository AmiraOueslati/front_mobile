package com.example.myapplication11.models

import com.google.gson.annotations.SerializedName

data class Product(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("prix")
	val prix: Any? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("nom")
	val nom: String? = null
)
