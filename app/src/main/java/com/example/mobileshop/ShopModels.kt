package com.example.mobileshop

import java.util.Locale

data class ShopItem(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val priceCents: Int,
    val imageResId: Int
)

data class BagLine(
    val item: ShopItem,
    val quantity: Int
) {
    val lineTotalCents: Int
        get() = item.priceCents * quantity
}

fun calculateBagTotalCents(lines: List<BagLine>): Int =
    lines.sumOf { it.lineTotalCents }

fun calculateBagItemCount(lines: List<BagLine>): Int =
    lines.sumOf { it.quantity }

fun formatCurrency(cents: Int): String =
    String.format(Locale.US, "$%,.2f", cents / 100.0)

fun shopInventory(): List<ShopItem> = listOf(
    ShopItem(
        id = 1,
        name = "Studio Headphones",
        category = "Tech",
        description = "Wireless sound with soft ear cushions.",
        priceCents = 7999,
        imageResId = R.drawable.product_headphones
    ),
    ShopItem(
        id = 2,
        name = "Everyday Backpack",
        category = "Travel",
        description = "Water-resistant bag with laptop space.",
        priceCents = 6450,
        imageResId = R.drawable.product_backpack
    ),
    ShopItem(
        id = 3,
        name = "Runner Sneakers",
        category = "Style",
        description = "Lightweight sneakers for busy days.",
        priceCents = 9250,
        imageResId = R.drawable.product_sneakers
    ),
    ShopItem(
        id = 4,
        name = "Smart Watch",
        category = "Tech",
        description = "Tracks steps, sleep, and notifications.",
        priceCents = 12999,
        imageResId = R.drawable.product_watch
    ),
    ShopItem(
        id = 5,
        name = "Desk Lamp",
        category = "Home",
        description = "Warm adjustable light for study time.",
        priceCents = 3850,
        imageResId = R.drawable.product_lamp
    ),
    ShopItem(
        id = 6,
        name = "Canvas Notebook",
        category = "Study",
        description = "Hardcover dotted pages with ribbon marker.",
        priceCents = 1899,
        imageResId = R.drawable.product_notebook
    )
)
