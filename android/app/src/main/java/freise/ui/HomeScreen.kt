package java.freise.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val Ink = Color(0xFF021610)
private val DeepGreen = Color(0xFF003C2C)
private val CardGreen = Color(0xFF07392E)
private val Moss = Color(0xFF86D759)
private val Tomato = Color(0xFFFF5A35)
private val Cream = Color(0xFFFFF7DF)
private val Muted = Color(0xFFA8BCAF)

data class FoodItem(
    val emoji: String,
    val name: String,
    val location: String,
    val note: String,
    val time: String,
    val status: String,
    val urgencyColor: Color
)

private val expiringItems = listOf(
    FoodItem("🍅", "Tomato", "Counter bowl", "sensor flag: mold trace", "2d", "Use first", Tomato),
    FoodItem("🍓", "Strawberries", "Top drawer", "moisture spike detected", "8h", "Tonight", Tomato),
    FoodItem("🥬", "Spinach", "Crisper", "great for a quick pesto", "1d", "Soon", Color(0xFFFFC86B)),
    FoodItem("🥩", "Steak slices", "Lower shelf", "keep sealed and cold", "2d", "Watch", Tomato),
    FoodItem("🥛", "Milk", "Door", "manual date tracked", "5d", "Safe", Moss)
)

@Composable
fun HomeScreen(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0D5A41), Ink, Color(0xFF010907)),
                    radius = 1150f
                )
            )
    ) {
        GlowBlob(Modifier.align(Alignment.TopEnd).offset(x = 70.dp, y = 84.dp), Tomato, .22f)
        GlowBlob(Modifier.align(Alignment.BottomStart).offset(x = (-80).dp, y = (-40).dp), Color(0xFF00A979), .18f)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(Modifier.height(26.dp)) }
            item { Header() }
            item { FridgeScanHero() }
            item { FreiseLevelCard(level = 8) }
            item { EatNextSection(expanded = expanded, onToggle = { expanded = !expanded }) }
            item { PreservationStrip() }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("freïse", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.2).sp)
            Text("Your fridge is watching 5 items right now", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        androidx.compose.foundation.Image(
            painter = painterResource(id = com.rork.freise.R.drawable.freise_logo),
            contentDescription = "Freïse logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(Color.White.copy(.08f))
                .border(1.dp, Color.White.copy(.18f), CircleShape)
                .padding(4.dp)
        )
    }
}

@Composable
private fun FridgeScanHero(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Box(
            Modifier
                .background(Brush.linearGradient(listOf(Color(0xFF124D3C), Color(0xFF432315), Color(0xFF092D25))))
                .border(1.dp, Color.White.copy(.12f), RoundedCornerShape(34.dp))
                .padding(22.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SensorBadge(icon = Icons.Default.Kitchen, color = Moss)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Your Fridge", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                        Text("Continuous background scan active", color = Color(0xFFC7D8C9), fontSize = 13.sp)
                    }
                    LivePill()
                }
                Spacer(Modifier.height(22.dp))
                Text("Strawberries may spoil tonight.", color = Color.White, fontSize = 29.sp, lineHeight = 31.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricTile("8h", "soonest", Tomato, Modifier.weight(1f))
                    MetricTile("5", "tracked", Moss, Modifier.weight(1f))
                    MetricTile("$18", "waste risk", Color(0xFFFFD174), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FreiseLevelCard(level: Int, modifier: Modifier = Modifier) {
    var showInfo by remember { mutableStateOf(false) }
    val levelColor = when (level) {
        in 1..3 -> Color(0xFFFF594D)
        in 4..7 -> Color(0xFFFFD45F)
        else -> Color(0xFF6FE870)
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) { Text("Got it") }
            },
            title = { Text("How Freïse Level works") },
            text = { Text("Freïse estimates freshness from ethylene gas patterns released by produce on the right side of your fridge. Rising ethylene levels can indicate accelerated ripening; unusual spikes paired with humidity changes may suggest mold risk.") }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF092D25).copy(.92f))
            .border(1.dp, levelColor.copy(.38f), RoundedCornerShape(28.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Freïse Level", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                IconButton(onClick = { showInfo = true }, modifier = Modifier.size(36.dp)) {
                    androidx.compose.material3.Icon(Icons.Default.Info, contentDescription = "Freïse Level information", tint = Muted, modifier = Modifier.size(21.dp))
                }
            }
            Text("Right-side produce freshness", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Text(level.toString(), color = levelColor, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Text("/10", color = Color.White.copy(.58f), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp, top = 12.dp))
    }
}

@Composable
private fun EatNextSection(expanded: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text("Your Food", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("Stacked by spoilage risk", color = Muted, fontSize = 14.sp)
            }
            Text(
                if (expanded) "Collapse" else "View all",
                color = Color(0xFFC9F5D3),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onToggle() }.padding(8.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(if (expanded) 442.dp else 188.dp)) {
            expiringItems.forEachIndexed { index, item ->
                if (expanded || index < 3) {
                    val y = if (expanded) (index * 88).dp else (index * 28).dp
                    val scale by animateFloatAsState(
                        targetValue = if (expanded) 1f else 1f - index * .035f,
                        animationSpec = tween(450, easing = FastOutSlowInEasing),
                        label = "stackScale"
                    )
                    FoodCard(
                        item = item,
                        modifier = Modifier
                            .offset(y = y)
                            .scale(scale)
                            .alpha(if (!expanded && index == 2) .72f else 1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodCard(item: FoodItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(CardGreen.copy(.92f))
            .border(1.dp, Color.White.copy(.09f), RoundedCornerShape(26.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF3C4234)), contentAlignment = Alignment.Center) {
            Text(item.emoji, fontSize = 28.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text("${item.location} • ${item.note}", color = Muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(item.time, color = item.urgencyColor, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(item.status, color = Color.White.copy(.72f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PreservationStrip(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Cream).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SensorBadge(Icons.Default.WarningAmber, Color(0xFFB37600), dark = true)
        Spacer(Modifier.width(14.dp))
        Column {
            Text("Background safety alerts", color = Color(0xFF251B06), fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text("Freïse nudges you before food crosses from edible to risky.", color = Color(0xFF75633B), fontSize = 13.sp)
        }
    }
}

@Composable
private fun MetricTile(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Color.White.copy(.08f)).border(1.dp, color.copy(.35f), RoundedCornerShape(18.dp)).padding(12.dp)) {
        Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(.72f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SensorBadge(icon: ImageVector, color: Color, modifier: Modifier = Modifier, dark: Boolean = false) {
    Box(modifier.size(46.dp).clip(CircleShape).background(color.copy(if (dark) .2f else .18f)), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(25.dp))
    }
}

@Composable
private fun LivePill(modifier: Modifier = Modifier) {
    Row(modifier.clip(CircleShape).background(Color(0xFF0A2B21).copy(.74f)).padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(Moss))
        Spacer(Modifier.width(6.dp))
        Text("LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun GlowBlob(modifier: Modifier, color: Color, alpha: Float) {
    Box(modifier.size(230.dp).clip(CircleShape).background(color.copy(alpha)))
}
