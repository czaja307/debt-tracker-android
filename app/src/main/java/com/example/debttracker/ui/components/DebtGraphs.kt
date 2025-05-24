package com.example.debttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debttracker.ui.theme.ComponentCornerRadiusBig
import com.example.debttracker.ui.theme.ComponentCornerRadiusSmall
import com.example.debttracker.ui.theme.TextSecondary
import com.example.debttracker.ui.theme.TilePrimary
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
fun DebtOverTimeGraph(
    data: Map<String, Float>
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {  // <- Use data as LaunchedEffect key to update on data change
        try {
            if (data.isEmpty()) {
                // Handle empty data case
                modelProducer.runTransaction {
                    lineSeries { series(listOf(0f)) }
                    extras { it[BottomAxisLabelKey] = listOf("No Data") }
                }
            } else {
                modelProducer.runTransaction {
                    lineSeries { series(data.values.toList()) }
                    extras { it[BottomAxisLabelKey] = data.keys.toList() }
                }
            }
        } catch (e: Exception) {
            // Handle exception (could log or show an error state)
            modelProducer.runTransaction {
                lineSeries { series(listOf(0f)) }
                extras { it[BottomAxisLabelKey] = listOf("Error") }
            }
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ComponentCornerRadiusSmall),
    ) {
        Column(modifier = Modifier.background(Color(0xFF1C1C1C))) {
            Text(
                text = "My current debt",
                fontSize = 20.sp,
                color = Color(0xFFE3E3E3),
                modifier = Modifier.padding(top = 24.dp, start = 24.dp)
            )
            Box(
                modifier = Modifier
                    .background(Color(0xFF1C1C1C))
                    .padding(start = 24.dp, end = 30.dp, bottom = 30.dp, top = 20.dp)
            ) {
                JetpackComposeBasicLineChart(modelProducer)
            }
        }
    }
}

@Composable
fun DebtPieChart(
    data: Map<String, PieChartData.Slice>, 
    title: String,
    currencySymbol: String = "$"
) {
    Box(
        modifier = Modifier
            .height(220.dp)
            .fillMaxWidth()
    ) {
        val totalValue = data.values.sumOf { it.value.toInt() }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ComponentCornerRadiusSmall),
        ) {
            Column(
                modifier = Modifier.background(TilePrimary)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 24.dp, start = 24.dp)
                )

                // Chart with weight
                Box(
                    modifier = Modifier
                        .background(TilePrimary)
                        .padding(top = 8.dp)
                        .weight(1f, fill = false) // Add weight but don't force filling
                ) {
                    PieChart(
                        pieChartData = PieChartData(slices = data.values.toList()),
                        sliceDrawer = SimpleSliceDrawer(100f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Set a reasonable height
                    )
                }

                // Legend with fixed content size
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .wrapContentHeight(), // Ensure it takes only needed height
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    data.forEach { (label, slice) ->
                        val percentage = (slice.value / totalValue * 100).toInt()
                        LegendItem(
                            color = slice.color,
                            label = label,
                            value = "$currencySymbol${slice.value.toInt()}",
                            percentage = "$percentage%",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color, label: String, value: String, percentage: String, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )

        // Label and values
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$value — $percentage", fontSize = 12.sp, color = Color(0xFFAAAAAA)
                )
            }
        }
    }
}