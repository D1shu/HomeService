package com.example.homeservice.models

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "customer", // "customer" or "provider"
    val profileImage: String = ""
)