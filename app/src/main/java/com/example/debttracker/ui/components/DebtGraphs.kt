package com.example.debttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.coroutines.runBlocking

private val data = mapOf(
    "Mon" to 40f,
    "Tue" to 30f,
    "Wed" to 50f,
    "Thu" to 70f,
    "Fri" to 110f,
    "Sat" to 100f,
    "Sun" to 20f,
)

@Composable
fun DebtOverTimeGraph() {
    val modelProducer = remember { CartesianChartModelProducer() }

    runBlocking {
        modelProducer.runTransaction {
            lineSeries { series(data.values) }
            extras { it[BottomAxisLabelKey] = data.keys.toList() }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1C1C1C))
                .padding(16.dp)
        ) {
            JetpackComposeBasicLineChart(modelProducer)
        }
    }
}

private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()

private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore[BottomAxisLabelKey][x.toInt()]
}


@Composable
private fun JetpackComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFFCDDC39)))
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = LineComponent(
                    fill(Color.White.copy(alpha = 0.2f))
                ), label = TextComponent(color = Color.White.toArgb())
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = LineComponent(fill(Color.White.copy(alpha = 0f))),
                label = TextComponent(color = Color.White.toArgb()),
                valueFormatter = BottomAxisValueFormatter
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
fun DebtWheelGraph() {
    val debtData = listOf(
        PieChartData.Slice(value = 40f, color = Color(0xFF3B4C00)), // Example: 40% in green
        PieChartData.Slice(value = 30f, color = Color(0xFFB4DD1E)), // Example: 30% in orange
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1C1C1C))
                .padding(16.dp)
        ) {
            PieChart(
                pieChartData = PieChartData(slices = debtData),
                sliceDrawer = SimpleSliceDrawer(100f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}


//@Composable
//fun DebtOverTimeGraph() {
//    val darkBackground = Color(0xFF1C1C1C) // TODO: Replace color with your theme's background color
//    val accentGreen = Color(0xFFCDDC39) // TODO: Replace color with your theme's accent color
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(220.dp),
//        colors = CardDefaults.cardColors(containerColor = darkBackground)
//    ) {
//        LineChart(
//            linesChartData = listOf(
//                LineChartData(
//                    points = listOf(
//                        LineChartData.Point(40f, "Mon"),
//                        LineChartData.Point(30f, "Tue"),
//                        LineChartData.Point(50f, "Wed"),
//                        LineChartData.Point(70f, "Thu"),
//                        LineChartData.Point(110f, "Fri"),
//                        LineChartData.Point(100f, "Sat"),
//                        LineChartData.Point(20f, "Sun")
//                    ),
//                    lineDrawer = SolidLineDrawer(color = accentGreen, thickness = 3.dp),
//                )
//            ), pointDrawer = NoPointDrawer, xAxisDrawer = SimpleXAxisDrawer(
//                axisLineColor = Color.White,
//                axisLineThickness = 1.dp,
//                labelTextColor = Color.White,
//                labelTextSize = 12.sp,
//            ), yAxisDrawer = SimpleYAxisDrawer(
//                axisLineColor = Color.White,
//                axisLineThickness = 1.dp,
//                labelTextColor = Color.White,
//                labelTextSize = 12.sp,
//            ), modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .padding(30.dp)
//        )
//    }
//}