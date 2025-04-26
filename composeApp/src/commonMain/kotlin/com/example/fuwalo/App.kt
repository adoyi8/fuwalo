package com.example.fuwalo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Image(painter = painterResource(Res.drawable.background), contentDescription = "", modifier = Modifier.fillMaxSize())
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

            Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp)){
                MenuItemCard(modifier = Modifier.weight(1f))
                MenuItemCard(modifier =Modifier.weight(1f) )
            }

            Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp)){
                CustomButton(modifier = Modifier.weight(1f).padding(10.dp))
                CustomButton(modifier = Modifier.weight(1f).padding(10.dp))
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
fun MenuItemCard(image: DrawableResource = Res.drawable.menu_item_piano, modifier: Modifier = Modifier){
    Card(modifier = modifier.width(174.dp).height(116.dp).padding(10.dp)){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Image(
                painter = painterResource(image),
                contentDescription = "",
                modifier = Modifier.size(100.dp).align(Alignment.Center)
            )
        }

    }
}
@Composable
fun CustomButton(modifier: Modifier = Modifier, text : String = "", border: BorderStroke? = null, textColor:Color = Color.Unspecified){
    Button(modifier = modifier, colors = ButtonDefaults.buttonColors(containerColor = Color(0xffC3BFF3)), onClick = {}, content = {
        Text(text = text, color = textColor)
    }, border = border)
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
          CustomButton(text = "RANDOM", textColor = Color.Black)
            Spacer(Modifier.width(10.dp))
            CustomButton(text = "START", textColor = Color.White)
        }


    }
}