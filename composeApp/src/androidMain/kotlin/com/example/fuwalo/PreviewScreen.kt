package com.example.fuwalo


import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.background



@Composable
fun PreviewScreen(){
 //PianoScreen()
}




@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"

)
@Composable
fun PianoScreen2(){
Home()
}



@Composable
fun PianoScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))
                )
            )
            .padding(16.dp)
    ) {
        Column {
            TopBar()
            Spacer(modifier = Modifier.height(24.dp))
            PianoGrid()
        }
        BottomControls(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFD1C4E9))
            )
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFD1C4E9))
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { /* Save */ },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // Replace with your icons
                    contentDescription = "Save",
                    tint = Color.Black
                )
            }
            IconButton(
                onClick = { /* Home */ },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home, // Replace with your icons
                    contentDescription = "Home",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun PianoGrid() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PianoCard(Color(0xFFB2EBF2), Color(0xFF80DEEA), modifier = Modifier.weight(1f))
            PianoCard(Color(0xFFD1C4E9), Color(0xFFB39DDB), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PianoCard(Color(0xFFB2EBF2), Color(0xFFE0F7FA), Modifier.weight(1f))
            PianoCard(Color(0xFFFFF9C4), Color(0xFFFFF59D), Modifier.weight(1f))
        }
    }
}

@Composable
fun PianoCard(startColor: Color, endColor: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(startColor, endColor))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Call, // Replace with your piano icon
            contentDescription = "Piano",
            tint = Color.Unspecified,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun BottomControls(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(Color.White, shape = CircleShape)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            onClick = { /* Up */ },
            modifier = Modifier
                .background(Color(0xFFD1C4E9), shape = CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp, // Replace with your icon
                contentDescription = "Up",
                tint = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFD1C4E9))
        )

        Button(
            onClick = { /* Random */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD1C4E9),
                contentColor = Color.Black
            ),
            shape = CircleShape
        ) {
            Text(text = "RANDOM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
