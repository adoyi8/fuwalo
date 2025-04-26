package com.example.fuwalo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.background
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
@Preview
fun App() {
    MaterialTheme {
       // PianoScreen()
        Image(painter = painterResource(Res.drawable.background),
            contentDescription = "Background", modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun Home(){
Box(modifier = Modifier.fillMaxSize()){
    Image(painter = painterResource(Res.drawable.background), contentDescription = "")
    Box(modifier = Modifier.fillMaxSize()){

    }
}
}