package com.example.fuwalo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        Row(modifier = Modifier.fillMaxWidth().height(20.dp).padding(16.dp), horizontalArrangement = Arrangement.SpaceAround){
            Row(modifier = Modifier.weight(2f)){
                Spacer(modifier = Modifier.weight(0.1f))
                CustomButton(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(0.1f))
                CustomButton(modifier = Modifier.weight(2f))
            }
            Row(Modifier.weight(7f)){
                
            }
           
        }

    }

}
}


@Composable
fun CustomButton(modifier: Modifier = Modifier, text : String = ""){
    Button(modifier = modifier, colors = ButtonDefaults.buttonColors(containerColor = Color(0xffC3BFF3)), onClick = {}, content = {
        Text(text)
    })    
}