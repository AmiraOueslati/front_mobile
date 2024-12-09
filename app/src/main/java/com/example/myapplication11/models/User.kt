package com.example.myapplication11.models

import com.google.gson.annotations.SerializedName

data class User(
	@field:SerializedName("password") val password: String? = null,
	@field:SerializedName("phoneNumber") val phoneNumber: String? = null,
	@field:SerializedName("birthDate") val birthDate: String? = null,
	@field:SerializedName("email") val email: String? = null,
	@field:SerializedName("username") val username: String? = null
)
