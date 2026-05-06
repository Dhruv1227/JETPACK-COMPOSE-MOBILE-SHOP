package com.example.mobileshop

import org.junit.Assert.assertEquals
import org.junit.Test

class ShopModelsTest {
    @Test
    fun bagTotalCountsQuantities() {
        val notebook = ShopItem(
            id = 1,
            name = "Notebook",
            category = "Study",
            description = "Test item",
            priceCents = 1899,
            imageResId = 0
        )
        val headphones = ShopItem(
            id = 2,
            name = "Headphones",
            category = "Tech",
            description = "Test item",
            priceCents = 7999,
            imageResId = 0
        )

        val lines = listOf(
            BagLine(notebook, quantity = 3),
            BagLine(headphones, quantity = 2)
        )

        assertEquals(21695, calculateBagTotalCents(lines))
        assertEquals(5, calculateBagItemCount(lines))
    }

    @Test
    fun currencyFormattingUsesDollarsAndCents() {
        assertEquals("$129.99", formatCurrency(12999))
    }
}
