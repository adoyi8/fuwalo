package com.example.fuwalo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform