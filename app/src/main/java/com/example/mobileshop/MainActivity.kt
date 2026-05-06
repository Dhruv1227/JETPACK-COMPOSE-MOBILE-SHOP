package com.example.mobileshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileShopTheme {
                ShopApp()
            }
        }
    }
}

@Composable
fun MobileShopTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Color(0xFF266D55),
        onPrimary = Color.White,
        secondary = Color(0xFF695D27),
        tertiary = Color(0xFF8C4F2B),
        background = Color(0xFFF7F4EF),
        surface = Color(0xFFFFFBF6),
        surfaceVariant = Color(0xFFE5DED3),
        onBackground = Color(0xFF1E1F1B),
        onSurface = Color(0xFF1E1F1B)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}

@Composable
fun ShopApp() {
    val products = remember { shopInventory() }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var quantities by rememberSaveable { mutableStateOf(emptyMap<Int, Int>()) }
    var isCartOpen by rememberSaveable { mutableStateOf(false) }
    var isPaymentOpen by rememberSaveable { mutableStateOf(false) }
    val categories = remember(products) { listOf("All") + products.map { it.category }.distinct() }
    val visibleProducts = products.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }
    val bagLines = products.mapNotNull { item ->
        val quantity = quantities[item.id] ?: 0
        if (quantity > 0) BagLine(item, quantity) else null
    }
    val totalCents = calculateBagTotalCents(bagLines)
    val itemCount = calculateBagItemCount(bagLines)

    fun updateQuantity(item: ShopItem, change: Int) {
        val nextQuantity = ((quantities[item.id] ?: 0) + change).coerceAtLeast(0)
        quantities = if (nextQuantity == 0) {
            quantities - item.id
        } else {
            quantities + (item.id to nextQuantity)
        }
    }

    fun checkout() {
        quantities = emptyMap()
        isCartOpen = false
        isPaymentOpen = false
    }

    fun startCheckout() {
        if (itemCount > 0) {
            isPaymentOpen = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth >= 780.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isWide) {
                    BottomBagBar(
                        itemCount = itemCount,
                        totalCents = totalCents,
                        onOpenCart = { isCartOpen = true }
                    )
                }
            }
        ) { innerPadding ->
            if (isWide) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CatalogPane(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        products = visibleProducts,
                        quantities = quantities,
                        onAdd = { updateQuantity(it, 1) },
                        onRemove = { updateQuantity(it, -1) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        useGrid = true
                    )
                    BagPanel(
                        lines = bagLines,
                        totalCents = totalCents,
                        onAdd = { updateQuantity(it, 1) },
                        onRemove = { updateQuantity(it, -1) },
                        onCheckout = { startCheckout() },
                        modifier = Modifier
                            .width(340.dp)
                            .fillMaxHeight()
                    )
                }
            } else {
                CatalogPane(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    products = visibleProducts,
                    quantities = quantities,
                    onAdd = { updateQuantity(it, 1) },
                    onRemove = { updateQuantity(it, -1) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    compactBagLines = bagLines,
                    compactTotalCents = totalCents,
                    onOpenCart = { isCartOpen = true },
                    useGrid = false
                )
            }
        }

        if (!isWide && isCartOpen) {
            CartDialog(
                lines = bagLines,
                totalCents = totalCents,
                onAdd = { updateQuantity(it, 1) },
                onRemove = { updateQuantity(it, -1) },
                onCheckout = { startCheckout() },
                onDismiss = { isCartOpen = false }
            )
        }

        if (isPaymentOpen) {
            SimulatedPaymentDialog(
                totalCents = totalCents,
                onPay = { checkout() },
                onDismiss = { isPaymentOpen = false }
            )
        }
    }
}

@Composable
private fun CatalogPane(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    products: List<ShopItem>,
    quantities: Map<Int, Int>,
    onAdd: (ShopItem) -> Unit,
    onRemove: (ShopItem) -> Unit,
    modifier: Modifier = Modifier,
    compactBagLines: List<BagLine> = emptyList(),
    compactTotalCents: Int = 0,
    onOpenCart: () -> Unit = {},
    useGrid: Boolean
) {
    Column(modifier = modifier) {
        ShopHeader()
        CategoryFilter(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        if (useGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 260.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { item ->
                    ProductCard(
                        item = item,
                        quantity = quantities[item.id] ?: 0,
                        onAdd = { onAdd(item) },
                        onRemove = { onRemove(item) }
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { item ->
                    ProductCard(
                        item = item,
                        quantity = quantities[item.id] ?: 0,
                        onAdd = { onAdd(item) },
                        onRemove = { onRemove(item) }
                    )
                }
                item {
                    CompactBagSummary(
                        lines = compactBagLines,
                        totalCents = compactTotalCents,
                        onOpenCart = onOpenCart
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(76.dp))
                }
            }
        }
    }
}

@Composable
private fun ShopHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mobile Shop",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Pick items and watch your cart update.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "$",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun ProductCard(
    item: ShopItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(92.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category.uppercase(Locale.US),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatCurrency(item.priceCents),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    QuantityControl(
                        quantity = quantity,
                        onAdd = onAdd,
                        onRemove = onRemove
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityControl(
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    if (quantity == 0) {
        Button(
            onClick = onAdd,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text("Add")
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onRemove,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Text("-")
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun BagPanel(
    lines: List<BagLine>,
    totalCents: Int,
    onAdd: (ShopItem) -> Unit,
    onRemove: (ShopItem) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add to Cart",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                if (lines.isNotEmpty()) {
                    TextButton(onClick = onCheckout) {
                        Text("Checkout")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (lines.isEmpty()) {
                EmptyBagMessage()
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(lines, key = { it.item.id }) { line ->
                        BagLineRow(
                            line = line,
                            onAdd = { onAdd(line.item) },
                            onRemove = { onRemove(line.item) }
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Cart total",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                    )
                    Text(
                        text = "${calculateBagItemCount(lines)} item(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                    )
                }
                Text(
                    text = formatCurrency(totalCents),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun BagLineRow(
    line: BagLine,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = line.item.imageResId),
            contentDescription = line.item.name,
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(6.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = line.item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${line.quantity} x ${formatCurrency(line.item.priceCents)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
            )
        }
        QuantityControl(
            quantity = line.quantity,
            onAdd = onAdd,
            onRemove = onRemove
        )
    }
}

@Composable
private fun CompactBagSummary(
    lines: List<BagLine>,
    totalCents: Int,
    onOpenCart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cart",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                TextButton(onClick = onOpenCart) {
                    Text("Open")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (lines.isEmpty()) {
                EmptyBagMessage()
            } else {
                lines.forEach { line ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${line.quantity} x ${line.item.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatCurrency(line.lineTotalCents),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text(formatCurrency(totalCents), fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun EmptyBagMessage() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Your cart is empty.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun BottomBagBar(
    itemCount: Int,
    totalCents: Int,
    onOpenCart: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (itemCount == 0) "Cart empty" else "$itemCount item(s)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                )
                Text(
                    text = formatCurrency(totalCents),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Button(
                onClick = onOpenCart,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)
                )
            ) {
                Text("Add to Cart")
            }
        }
    }
}

@Composable
private fun CartDialog(
    lines: List<BagLine>,
    totalCents: Int,
    onAdd: (ShopItem) -> Unit,
    onRemove: (ShopItem) -> Unit,
    onCheckout: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cart",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (lines.isEmpty()) {
                    EmptyBagMessage()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(lines, key = { it.item.id }) { line ->
                            BagLineRow(
                                line = line,
                                onAdd = { onAdd(line.item) },
                                onRemove = { onRemove(line.item) }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Cart total",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                        Text(
                            text = "${calculateBagItemCount(lines)} item(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                    }
                    Text(
                        text = formatCurrency(totalCents),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onCheckout,
                    enabled = lines.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)
                    )
                ) {
                    Text("Checkout")
                }
            }
        }
    }
}

@Composable
private fun SimulatedPaymentDialog(
    totalCents: Int,
    onPay: () -> Unit,
    onDismiss: () -> Unit
) {
    var paymentMethod by rememberSaveable { mutableStateOf("Demo Wallet") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Simulation only. No real card details are collected or stored.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Payment method",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PaymentOptionButton(
                        label = "Demo Wallet",
                        selected = paymentMethod == "Demo Wallet",
                        onClick = { paymentMethod = "Demo Wallet" },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentOptionButton(
                        label = "Demo Card",
                        selected = paymentMethod == "Demo Card",
                        onClick = { paymentMethod = "Demo Card" },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Total due",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                        Text(
                            text = paymentMethod,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                    }
                    Text(
                        text = formatCurrency(totalCents),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onPay,
                    enabled = totalCents > 0,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)
                    )
                ) {
                    Text("Pay ${formatCurrency(totalCents)}")
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
        ) {
            Text(label)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
        ) {
            Text(label)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ShopAppPreview() {
    MobileShopTheme {
        ShopApp()
    }
}
