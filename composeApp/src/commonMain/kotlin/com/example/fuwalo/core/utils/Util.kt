package com.example.fuwalo.core.utils

import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fuwalo.core.utils.Util.WHITE_KEY_WIDTH
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


object Util {
    val WHITE_KEY_WIDTH= 80.dp;
    val WHITE_KEY_HEIGHT= 240.dp;
    val BLACK_KEY_WIDTH = 48.dp;
    val BLACK_KEY_HEIGHT = 160.dp
    val buttonsColor = Color(0xff7f90c6)




    fun getBlackKeyOffSet(position: Int): Dp {
       return (WHITE_KEY_WIDTH.times(position-1))+ (WHITE_KEY_WIDTH+WHITE_KEY_WIDTH-BLACK_KEY_WIDTH)/2
    }
}
