package com.example.agroagil.core.models


data class FarmModel(
    val name: String = "",
    val image: String = "",
    val members: List<Member> = emptyList()
)