package com.example.fuwalo

import kotlinx.serialization.Serializable

sealed interface Route {


    @Serializable
    data object PianoGraph: Route

    @Serializable
    data object HomeScreen: Route

    @Serializable
    data object KeyBoardScreen: Route

    @Serializable
    data object SplashScreen: Route
    @Serializable
    data object PianoLearningScreen: Route
}