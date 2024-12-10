package com.example.myapplication11.models

import com.google.gson.annotations.SerializedName

data class Product(

	@field:SerializedName("imageUrl") // Correspond au champ imageUrl dans Node.js
	val imageUrl: String? = null,

	@field:SerializedName("prix")
	val prix: Double? = null, // Utilisez Double si vous attendez un prix numérique

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("name") // Correspond au champ name dans Node.js
	val name: String? = null,

	@field:SerializedName("state") // Correspond au champ state dans Node.js
	val state: String? = null,

	@field:SerializedName("location") // Correspond au champ location dans Node.js
	val location: String? = null,

	@field:SerializedName("category") // Correspond au champ category dans Node.js
	val category: String? = null
)

