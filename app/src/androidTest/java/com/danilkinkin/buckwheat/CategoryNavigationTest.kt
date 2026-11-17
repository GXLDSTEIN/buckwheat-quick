package com.danilkinkin.buckwheat

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.danilkinkin.buckwheat.analytics.CategoryExpensesContent
import com.danilkinkin.buckwheat.analytics.expensesForCategory
import com.danilkinkin.buckwheat.data.ExtendCurrency
import com.danilkinkin.buckwheat.data.entities.Category
import com.danilkinkin.buckwheat.data.entities.Transaction
import com.danilkinkin.buckwheat.data.entities.TransactionType
import com.danilkinkin.buckwheat.editor.tagging.CategoryPicker
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.Date

@RunWith(AndroidJUnit4::class)
class CategoryNavigationTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun categories() = (1..80).map { Category(id = "cat-$it", name = "Category $it") } +
        Category(id = "health", name = "Медицина")

    @Test fun pickerShowsManyCategoriesWithoutHorizontalScrolling() {
        compose.setContent { MaterialTheme { CategoryPicker(categories(), null, {}, {}, {}) } }
        assertTrue(compose.onAllNodes(isSelectable()).fetchSemanticsNodes().size >= 8)
        compose.onNodeWithText(context.getString(R.string.quick_add_category)).assertIsDisplayed()
    }

    @Test fun searchSelectsADistantCategoryCaseInsensitively() {
        var selected: String? = null
        compose.setContent { MaterialTheme { CategoryPicker(categories(), null, { selected = it }, {}, {}) } }
        compose.onNode(hasSetTextAction()).performTextInput("мед")
        compose.onNodeWithText("Медицина").performClick()
        compose.runOnIdle { assertEquals("health", selected) }
        compose.onNodeWithText("Category 1").assertDoesNotExist()
    }

    @Test fun choosingNoCategoryClearsSelectionAndCreateUsesSearchText() {
        var selected: String? = "health"
        var create: String? = null
        compose.setContent { MaterialTheme { CategoryPicker(categories(), selected, { selected = it }, { create = it }, {}) } }
        compose.onNodeWithText(context.getString(R.string.quick_uncategorized)).performClick()
        compose.runOnIdle { assertNull(selected) }
        compose.onNode(hasSetTextAction()).performTextInput("New category")
        compose.onNodeWithText(context.getString(R.string.quick_add_category)).performClick()
        compose.runOnIdle { assertEquals("New category", create) }
    }

    private fun expense(uid: Int, id: String?, comment: String, type: TransactionType = TransactionType.SPENT) =
        Transaction(type, BigDecimal("12.50"), Date(uid.toLong()), comment, categoryId = id).also { it.uid = uid }

    @Test fun categoryViewShowsOnlyItsExpensesAndPreservesComments() {
        val expenses = listOf(expense(1, "food", "First groceries"), expense(2, "transport", "Taxi"),
            expense(3, "food", "Second groceries"), expense(4, "food", "Income", TransactionType.INCOME))
        val filtered = expensesForCategory(expenses, "food")
        assertEquals(listOf(3, 1), filtered.map { it.uid })
        compose.setContent { MaterialTheme { CategoryExpensesContent("Groceries", filtered, ExtendCurrency.none(), {}) } }
        compose.onNodeWithText("First groceries").assertIsDisplayed()
        compose.onNodeWithText("Second groceries").assertIsDisplayed()
        compose.onNodeWithText("Taxi").assertDoesNotExist()
        compose.onNodeWithText("Income").assertDoesNotExist()
        compose.onNodeWithText(context.getString(R.string.quick_expense_count, 2)).assertIsDisplayed()
    }

    @Test fun uncategorizedAndEmptyCategoryRemainDistinct() {
        val expenses = listOf(expense(1, null, "Without category"), expense(2, "food", "Food"))
        assertEquals(listOf(1), expensesForCategory(expenses, null).map { it.uid })
        val filtered = expensesForCategory(expenses, "missing")
        assertTrue(filtered.isEmpty())
        compose.setContent { MaterialTheme { CategoryExpensesContent("Empty category", filtered, ExtendCurrency.none(), {}) } }
        compose.onNodeWithText(context.getString(R.string.quick_category_empty)).assertIsDisplayed()
        compose.onNodeWithText("Food").assertDoesNotExist()
    }
}
