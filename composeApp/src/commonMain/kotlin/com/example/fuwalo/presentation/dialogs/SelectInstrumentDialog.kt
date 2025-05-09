package com.example.fuwalo.presentation.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fuwalo.CustomButton
import com.example.fuwalo.data.loadSoundFonts
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.add_icon
import fuwalo.composeapp.generated.resources.select_instrument_guitar
import fuwalo.composeapp.generated.resources.select_instrument_piano
import fuwalo.composeapp.generated.resources.select_instrument_trumpet
import kotlinx.serialization.json.JsonNull.content
import org.jetbrains.compose.resources.painterResource

@Composable
fun SelectInstrumentDialog( showDialog: MutableTransitionState<Boolean>){
    AnimatedVisibility(showDialog) {
        AlertDialog(
            onDismissRequest = {

            },
            confirmButton = {

            },
            dismissButton = {
                CustomButton(text = "Cancel",
                    onClick = {
                    showDialog.targetState = false
                })
            },
            text = {
                Column(){
                    Row(modifier = Modifier.fillMaxWidth(70f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                        Image(
                            painter = painterResource(Res.drawable.select_instrument_piano),
                            contentDescription = "",
                            modifier = Modifier.size(75.dp)
                                .clickable(
                                    onClick = {
                                        loadSoundFonts("KawaiStereoGrand.sf3")
                                        showDialog.targetState = false
                                    }
                                )
                        )
                        Image(
                            painter = painterResource(Res.drawable.select_instrument_guitar),
                            contentDescription = "",
                            modifier = Modifier.size(75.dp)
                                .clickable(
                                    onClick = {
                                        loadSoundFonts("instrument_19.sf2")
                                        showDialog.targetState = false
                                    }
                                )
                        )
                        Image(
                            painter = painterResource(Res.drawable.select_instrument_trumpet),
                            contentDescription = "",
                            modifier = Modifier.size(75.dp)
                                .clickable(
                                    onClick = {
                                        loadSoundFonts("instrument_39.sf2")
                                        showDialog.targetState = false
                                    }
                                )
                        )

                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 50.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                        Image(
                            painter = painterResource(Res.drawable.add_icon),
                            contentDescription = "",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        )
    }
}