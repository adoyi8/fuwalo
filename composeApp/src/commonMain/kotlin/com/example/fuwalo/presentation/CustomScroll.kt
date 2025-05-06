import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.fuwalo.core.utils.Util.buttonsColor
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.mini_full_paino
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt// Make sure to replace with your actual package name

/**
 * A custom horizontal scrollbar with a piano image track, a colored hue, and a clear thumb area.
 *
 * @param scrollState The ScrollState of the scrollable content.
 * @param modifier The modifier to be applied to the scrollbar.
 * @param trackColor The background color of the scrollbar track (less relevant with the piano image and hue).
 * @param thumbColor The color of the draggable thumb's border or outline.
 * @param hueColor The color to apply as a hue over the piano image track outside the thumb area.
 */
@Composable
fun CustomHorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.Magenta, // This might be less visible with the image and hue
    thumbColor: Color = Color.Gray, // Used for the thumb's border
    hueColor: Color = buttonsColor.copy(alpha = 0.5f) // Default semi-transparent blue hue
) {
    var containerWidth by remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope() // Use a CoroutineScope for scrolling actions

    // Obtain the painter resource within the @Composable scope
    val pianoPainter: Painter = painterResource(Res.drawable.mini_full_paino) // Correctly obtain the painter

    // Calculate thumb properties based on scroll state and container width
    val totalContentWidth = scrollState.maxValue + containerWidth
    // Avoid division by zero if content is smaller than container or maxValue is 0
    val proportion = if (totalContentWidth > 0) containerWidth.toFloat() / totalContentWidth.toFloat() else 0f
    val density = LocalDensity.current
    val minThumbWidthPx = with(density) { 24.dp.toPx() } // Minimum thumb width in pixels
    val thumbWidthPx = (containerWidth.toFloat() * proportion).coerceAtLeast(minThumbWidthPx)

    val scrollRatio = if (scrollState.maxValue > 0) scrollState.value.toFloat() / scrollState.maxValue.toFloat() else 0f
    val thumbOffsetPx = scrollRatio * (containerWidth.toFloat() - thumbWidthPx)

    Box(
        modifier = modifier
            .onGloballyPositioned {
                containerWidth = it.size.width
            }
            .background(trackColor) // Outer background, might be partially visible
            .height(35.dp) // Set a fixed height for the scrollbar
            .fillMaxWidth()
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(70.dp)) // Track border (replace Color.Black with your buttonsColor)
            .clip(RoundedCornerShape(35.dp)) // Clip the drawing and content to the rounded shape
            .drawWithContent {
                // 1. Draw the base image (piano) as the background
                val imageSize = size // Size of the drawing area
                with(pianoPainter) {
                    draw(size = imageSize)
                }

                // 2. Draw the colored hue overlay BEFORE the thumb's cleared area
                if (thumbOffsetPx > 0) {
                    drawRect(
                        color = hueColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(thumbOffsetPx, imageSize.height),
                        blendMode = BlendMode.Multiply // Apply hue blend mode
                    )
                }

                // 3. Draw the colored hue overlay AFTER the thumb's cleared area
                val areaAfterThumbStart = thumbOffsetPx + thumbWidthPx
                val areaAfterThumbWidth = imageSize.width - areaAfterThumbStart
                if (areaAfterThumbWidth > 0) {
                    drawRect(
                        color = hueColor,
                        topLeft = Offset(areaAfterThumbStart, 0f),
                        size = Size(areaAfterThumbWidth, imageSize.height),
                        blendMode = BlendMode.Multiply // Apply hue blend mode
                    )
                }

                // The area under the thumb (between the two hue rectangles) is left untouched,
                // showing the original piano image.

                // Do NOT call drawContent() here in the same way as before,
                // as the thumb is a separate composable drawn on top by the layout system.
            }
    ) {
        // This Box represents the draggable thumb.
        // It is positioned using offset and handles the drag gestures.
        // It does NOT have a solid background that would cover the image.
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetPx.roundToInt(), 0) } // Position the thumb composable
                .width(with(density) { thumbWidthPx.toDp() }) // Set the width of the thumb composable
                .height(35.dp) // Match height
                // Add a border to visually represent the thumb without hiding the image below
                .border(width = 1.dp, color = thumbColor, shape = RoundedCornerShape(2.dp))
                .pointerInput(scrollState, containerWidth) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        val scrollableWidth = containerWidth.toFloat() - thumbWidthPx
                        if (scrollableWidth > 0) {
                            val deltaRatio = dragAmount / scrollableWidth
                            val newScrollValue = (scrollState.value + deltaRatio * scrollState.maxValue)
                                .roundToInt()
                                .coerceIn(0, scrollState.maxValue)
                            coroutineScope.launch {
                                scrollState.scrollTo(newScrollValue)
                            }
                        }
                    }
                }
            // REMOVE the solid background here
            // .background(thumbColor, shape = RoundedCornerShape(2.dp))
        )
    }
}