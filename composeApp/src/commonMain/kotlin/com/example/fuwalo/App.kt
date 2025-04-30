package com.example.fuwalo

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.fuwalo.presentation.NavigationViewModel
import com.example.fuwalo.presentation.SplashScreen
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.background
import fuwalo.composeapp.generated.resources.bottom_control_arrow_up
import fuwalo.composeapp.generated.resources.fu_menu
import fuwalo.composeapp.generated.resources.fuhome
import fuwalo.composeapp.generated.resources.menu_item_piano
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
@Preview
fun App(onKeyPress: (Int) -> Unit) {
    val navController = rememberNavController()



    LaunchedEffect(Unit) {
        NavigationViewModel.navController=navController
    }
    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = Route.PianoGraph
        ) {

            navigation<Route.PianoGraph>(
                startDestination = Route.HomeScreen
            ) {
                composable<Route.SplashScreen>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {

                    SplashScreen()
                }
                composable<Route.HomeScreen>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    Home()
                }

                composable<Route.KeyBoardScreen>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {

                    PianoScreen(onKeyPress = onKeyPress)
                }


            }

        }

    }
}

@Composable
fun Home(){
Box(modifier = Modifier.fillMaxSize()){
    Image(painter = painterResource(Res.drawable.background), contentDescription = "", modifier = Modifier.fillMaxSize())

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround){
            Row(modifier = Modifier.weight(2f)){
                Spacer(modifier = Modifier.weight(0.1f))
                CustomButton(modifier = Modifier.weight(1f).height(20.dp))
                Spacer(modifier = Modifier.weight(0.1f))
                CustomButton(modifier = Modifier.weight(2f).height(20.dp))
            }
            Row(Modifier.weight(7f)){

            }


    }
    Row(modifier = Modifier.fillMaxSize().background(Color.Transparent)){
        Row(modifier = Modifier.fillMaxHeight().weight(1f)){

        }
        Column(modifier = Modifier.fillMaxHeight().weight(1f).background(color = Color(0x0F0F1669))){
            Card(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp), colors = CardDefaults.cardColors(contentColor = Color(0XFFFED2E200))){
            Row(modifier = Modifier.fillMaxWidth().height(45.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically){
                Spacer(Modifier.weight(4f))
                Row(Modifier.weight(2f)) {
                    MenuCard(image = Res.drawable.fu_menu)
                    Spacer(modifier = Modifier.weight(2.5f))
                    MenuCard(image = Res.drawable.fuhome)
                }
            }
                }

            Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)){
                MenuItemCard(modifier = Modifier.weight(1f), cardContainerColor = Color(0Xffa6d6d6))
                MenuItemCard(modifier =Modifier.weight(1f), cardContainerColor = Color(0xffa59efe))
            }

            Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)){
                CustomButton(modifier = Modifier.weight(1f).height(70.dp).padding(10.dp), buttonColor = Color(0Xffa6d6d6), shape = RoundedCornerShape(6.dp))
                CustomButton(modifier = Modifier.weight(1f).height(70.dp).padding(10.dp), buttonColor = Color(0Xffd5d0a6), shape = RoundedCornerShape(6.dp))
            }



        }
    }
    Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(start = 16.dp, top = 16.dp, end = 16.dp)){
        BottomControls()
    }
}
}



@Preview
@Composable
fun MenuCard(image: DrawableResource = Res.drawable.background){
    Card(){
        Image(
            painter = painterResource(image),
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview
@Composable
fun MenuItemCard(image: DrawableResource = Res.drawable.menu_item_piano, modifier: Modifier = Modifier, cardContainerColor:Color = Color.Unspecified){
    Card(modifier = modifier.width(194.dp).height(160.dp).padding(top = 10.dp, start = 10.dp, end = 10.dp), colors = CardDefaults.cardColors(contentColor = cardContainerColor)){
        Box(modifier = Modifier.fillMaxSize().background(cardContainerColor), contentAlignment = Alignment.Center){
            Image(
                painter = painterResource(image),
                contentDescription = "",
                modifier = Modifier.size(100.dp).align(Alignment.Center)
            )
        }

    }
}
@Composable
fun CustomButton(modifier: Modifier = Modifier, text : String = "", border: BorderStroke? = null, textColor:Color = Color.Unspecified, buttonColor:Color = Color(0xffC3BFF3), shape: Shape = ButtonDefaults.shape, onClick:()->Unit = {}){
    Button(modifier = modifier, shape = shape, colors = ButtonDefaults.buttonColors(containerColor = buttonColor), onClick = onClick, content = {
        Text(text = text, color = textColor)
    }, border = border)
}


@Composable
fun CustomText(text: String, size: TextUnit = 16.sp, color: Color = MaterialTheme.colorScheme.onBackground, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start, fontWeight: FontWeight = FontWeight.Normal, textDecoration: TextDecoration = TextDecoration.None){

//    val fonts = listOf(
//        Font(resource = "fonts/tilt_neon.ttf"),
//    )
//    val fontFamily = FontFamily(
//        Font(
//            resource = "fonts/montserrat.ttf",
//            weight = FontWeight.W400,
//            style = FontStyle.Normal
//        )
//    )

    Text(modifier = modifier, text = text, textAlign = textAlign, style = TextStyle(fontSize = size,color = color, fontWeight = fontWeight, textDecoration = textDecoration))
}

@Composable
fun BottomControls(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color.White, shape = CircleShape)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(
                painter = painterResource(Res.drawable.bottom_control_arrow_up),
                contentDescription = "",
                modifier = Modifier
                    .background(Color(0xFFD1C4E9), shape = CircleShape)
                    .size(40.dp)
            )

            CustomButton(
                modifier =  Modifier.width(200.dp).height(35.dp),
                text = "00000000",
                border = BorderStroke(color = Color(0Xff6F71DE), width = 2.dp)
            )
        }
        Spacer(modifier = Modifier.weight(3f))

        Row(modifier = Modifier.weight(2f)){
          CustomButton(text = "RANDOM", textColor = Color.Black, border = BorderStroke(color = Color(0Xff6660BD), width = 1.dp), buttonColor = Color.White, shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.width(10.dp))
            CustomButton(onClick = {NavigationViewModel.navController?.navigate(Route.KeyBoardScreen)}, text = "START", textColor = Color.White, border = BorderStroke(color = Color(0Xff6660BD), width = 1.dp), buttonColor = Color(0Xff6660BD), shape = RoundedCornerShape(8.dp), )
        }


    }
}

@Composable
fun PianoScreen(onKeyPress: (Int) -> Unit) {

    val backgroundColor = remember{Color(0xffbec1ea)}
    val buttonsColor = remember{Color(0xff7f90c6)}
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f), horizontalArrangement = Arrangement.SpaceBetween){
            CustomButton(modifier = Modifier.weight(1f), buttonColor = buttonsColor)
            Spacer(modifier = Modifier.weight(5f))
            CustomButton(modifier = Modifier.weight(1f), buttonColor = buttonsColor)
        }
        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp), horizontalArrangement = Arrangement.Center){
            CustomButton(modifier = Modifier.fillMaxWidth(0.7f).height(50.dp), buttonColor = buttonsColor)
        }
        val whiteKeys = listOf(60, 62, 64, 65, 67, 69, 71, 72, 60 , 62) // C4 to C5
        PianoKeyboard(onKeyPress = onKeyPress)
    }
}
@Composable
fun PianoKeyboard(onKeyPress: (Int) -> Unit) {
    // val whiteKeys = listOf(60, 62, 64, 65, 67, 69, 71, 72)
    val whiteKeys = listOf(
        60, // C4
        62, // D4
        64, // E4
        65, // F4
        67, // G4
        69, // A4
        71, // B4
        62, // D4 (again)
        64  // E4 (again)
    )// C4 to C5

    val tones = listOf(
        Tone("C4",60),
        Tone("D4",62),
        Tone("E4",64),
        Tone("F4",65),
        Tone("G4",67),
        Tone("A4",69),
        Tone("B4",71),
        Tone("D4",62),
        Tone("E4",64),
    )// C4 to C5
    Row(modifier = Modifier.padding(8.dp)) {
        tones.forEach { tone ->
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 160.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .clickable { onKeyPress(tone.value) }
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            ){
                CustomText(tone.note, modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp), fontWeight = FontWeight.Bold)
            }
        }
    }
}
data class Tone(
    val note:String,
    val value:Int
)