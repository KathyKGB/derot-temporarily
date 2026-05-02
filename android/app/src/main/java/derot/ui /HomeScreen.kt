package com.rork.freshguard.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// colours used
private val Ink = Color(0xFF071611)
private val Moss = Color(0xFF12392D)
private val Mint = Color(0xFFB8FFD8)
private val Lime = Color(0xFF9DFF63)
private val Amber = Color(0xFFFFC46B)
private val Coral = Color(0xFFFF7058)
private val Cream = Color(0xFFFFF7E8)

// variables used
private data class FoodItem(
    val emoji: String,
    val name: String,
    val location: String,
    val status: String,
    val daysLeft: String,
    val color: Color,
    val note: String
)

private data class ActionCard(val title: String, val subtitle: String, val icon: ImageVector, val color: Color)

// user interface
@Composable
fun HomeScreen(navController: NavController) {
    val food = remember { //keeps data when UI is changed
        mutableStateListOf(
            FoodItem("🍓", "Strawberries", "Top fridge drawer", "Use tonight", "8h", Coral, "Moisture spike detected"),
            FoodItem("🥬", "Spinach", "Crisper", "Soon", "1d", Amber, "Great for a quick pesto"),
            FoodItem("🥩", "Steak slices", "Lower shelf", "Watch", "2d", Color(0xFFFF8E7C), "Keep sealed and cold"),
            FoodItem("🥛", "Milk", "Door", "Safe", "5d", Lime, "Manual date tracked")
        )
    }
    val actions = listOf(
        ActionCard("Scan produce", "Estimate freshness from image", Icons.Default.CameraAlt, Mint),
        ActionCard("Add item", "Milk, bread, leftovers", Icons.Default.Add, Amber),
        ActionCard("Fridge photo", "AI pantry inventory", Icons.Default.Inventory2, Color(0xFF8DEBFF)),
        ActionCard("Diet check", "Scan labels & risks", Icons.Default.Restaurant, Color(0xFFFFA8CF))
    )
    var dialog by remember { mutableStateOf<String?>(null) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) } //tracks which food item is clicked
    var itemName by remember { mutableStateOf("") } //tracks user input for adding food
    var itemLocation by remember { mutableStateOf("Fridge shelf") }

    //formatting
    Surface(color = Ink, modifier = Modifier.fillMaxSize()) {
        LazyColumn( //vertical scroll
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF03110D), Color(0xFF0E2B22), Color(0xFF071611))))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(Modifier.height(28.dp)) }
            item { Header(urgentCount = food.count { it.color == Coral || it.color == Amber }) } //filters items that are urgent
            item { FreshnessHero(onScan = { dialog = "scan" }) } 
            item {
                Text("Quick capture", color = Cream, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { //horizontal scroll
                    items(actions) { action -> ActionTile(action, onClick = { dialog = action.title }) } //loops through action list, click opens pop up
                }
            }
            item { SectionTitle("Eat next", "Tap an item to mark eaten or see tips") }
            items(food) { item -> FoodRow(item, onClick = { selectedFood = item }) }
            item { PreservationCoach(onClick = { dialog = "tips" }) } //clickable titles
            item { FriendAccountability(onClick = { dialog = "friends" }) }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (dialog != null) { //only shows dialog when something is selected
        val mode = dialog.orEmpty()
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = {
                Button(onClick = {
                    if (mode == "Add item" && itemName.isNotBlank()) { //adds item only if it is valid
                        food.add(0, FoodItem("🍽️", itemName.trim(), itemLocation.ifBlank { "Fridge shelf" }, "Tracked", "3d", Lime, "Added manually")) //adds item to top of the list
                        itemName = ""
                    }
                    dialog = null
                }) { Text(if (mode == "Add item") "Save" else "Got it") }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Close") } },
            title = { Text(dialogTitle(mode)) },
            text = {
                if (mode == "Add item") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Food name") }, singleLine = true)
                        OutlinedTextField(value = itemLocation, onValueChange = { itemLocation = it }, label = { Text("Where is it?") }, singleLine = true)
                        Text("FreshGuard will start a safe default 3-day timer. Later this can connect to camera/AI scanning.")
                    }
                } else {
                    Text(dialogBody(mode))
                }
            }
        )
    }

    selectedFood?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedFood = null },
            confirmButton = {
                Button(onClick = {
                    food.remove(item)
                    selectedFood = null
                }) { Text("Mark eaten") } //removes food item from list
            },
            dismissButton = { TextButton(onClick = { selectedFood = null }) { Text("Keep tracking") } },
            title = { Text("${item.emoji} ${item.name}") },
            text = { Text("${item.status}: ${item.daysLeft} left. ${item.note}. Tip: use this soon in a meal plan to avoid waste.") }
        )
    }
}

private fun dialogTitle(mode: String) = when (mode) {
    "scan" -> "Freshness scanner"
    "Scan produce" -> "Image freshness scan"
    "Fridge photo" -> "Pantry inventory scan"
    "Diet check" -> "Diet label check"
    "tips" -> "Preservation coach"
    "friends" -> "Friend accountability"
    else -> mode
}

private fun dialogBody(mode: String) = when (mode) {
    "scan", "Scan produce" -> "Point your camera at produce or meat to estimate freshness. In this prototype, manual tracking is live and camera AI is ready to connect next."
    "Fridge photo" -> "Take a fridge or pantry photo and FreshGuard will draft an inventory for review."
    "Diet check" -> "Scan an ingredient label to flag allergens, diet conflicts, and health concerns."
    "tips" -> "Store berries dry with airflow, herbs wrapped in a damp towel, and meat on the lowest shelf."
    "friends" -> "Maya saved spinach today. Share your save streak to reduce food waste together."
    else -> "Use this action to keep your fridge inventory accurate and avoid buying duplicates."
}

@Composable
private fun Header(urgentCount: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("FreshGuard", color = Cream, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text("Your fridge has $urgentCount items to save today", color = Color(0xFFB7CDBF), fontSize = 14.sp)
        }
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(Mint.copy(alpha = 0.14f)).border(1.dp, Mint.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Shield, contentDescription = "Freshness shield", tint = Mint, modifier = Modifier.size(28.dp)) }
    }
}

@Composable
private fun FreshnessHero(onScan: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF173F31), Color(0xFF284C19), Color(0xFF10231D)))).padding(22.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🥑", fontSize = 58.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Avocado says:", color = Mint, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("“I’m perfect for lunch.”", color = Cream, fontWeight = FontWeight.Black, fontSize = 25.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("4", "urgent", Coral)
                    MetricPill("9", "safe", Lime)
                    MetricPill("$18", "waste saved", Amber)
                }
                Spacer(Modifier.height(18.dp))
                Button(onClick = onScan, colors = ButtonDefaults.buttonColors(containerColor = Cream, contentColor = Ink), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan food freshness", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricPill(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = 0.11f)).border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(18.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(label, color = Cream.copy(alpha = 0.78f), fontSize = 12.sp)
    }
}

@Composable
private fun ActionTile(action: ActionCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.width(158.dp).clickable(onClick = onClick), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF10251F))) {
        Column(Modifier.padding(16.dp)) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(action.color.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) { Icon(action.icon, contentDescription = action.title, tint = action.color) }
            Spacer(Modifier.height(14.dp))
            Text(action.title, color = Cream, fontWeight = FontWeight.Bold)
            Text(action.subtitle, color = Color(0xFFA7BBB0), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Cream, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(subtitle, color = Color(0xFF91AA9D), fontSize = 12.sp)
        }
        Text("View all", color = Mint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun FoodRow(item: FoodItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color(0xFF0F241E)).border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp)).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(item.color.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) { Text(item.emoji, fontSize = 30.sp) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, color = Cream, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${item.location} • ${item.note}", color = Color(0xFF9EB5A8), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(item.daysLeft, color = item.color, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(item.status, color = Color(0xFFC8D7CD), fontSize = 12.sp)
        }
    }
}

@Composable
private fun PreservationCoach(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier.clickable(onClick = onClick).fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1C7))) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TipsAndUpdates, contentDescription = "Preservation tip", tint = Color(0xFF785000), modifier = Modifier.size(34.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Tip for tomatoes", color = Color(0xFF241907), fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text("Tap for more storage tricks that prevent waste.", color = Color(0xFF604A1C), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun FriendAccountability(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(Moss).clickable(onClick = onClick).padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Groups, contentDescription = "Friends", tint = Mint, modifier = Modifier.size(34.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text("Friend fridge streak", color = Cream, fontWeight = FontWeight.Bold)
            Text("Maya saved her spinach. Your turn before 8 PM.", color = Color(0xFFABC3B5), fontSize = 13.sp)
        }
        Icon(Icons.Default.CheckCircle, contentDescription = "Streak active", tint = Lime)
    }
}
