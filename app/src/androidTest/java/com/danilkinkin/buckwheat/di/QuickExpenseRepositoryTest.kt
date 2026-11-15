package com.danilkinkin.buckwheat.di

import android.content.Context
import android.content.ContextWrapper
import java.io.File
import androidx.datastore.preferences.core.edit
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danilkinkin.buckwheat.budgetDataStore
import com.danilkinkin.buckwheat.data.entities.Category
import com.danilkinkin.buckwheat.data.entities.Transaction
import com.danilkinkin.buckwheat.data.entities.TransactionType
import com.danilkinkin.buckwheat.util.toDate
import com.danilkinkin.buckwheat.util.toLocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Date

@RunWith(AndroidJUnit4::class)
class QuickExpenseRepositoryTest {
    private lateinit var context: Context
    private lateinit var db: DatabaseModule
    private lateinit var repository: SpendsRepository
    private val clock = object : GetCurrentDateUseCase() {
        var now: Date = LocalDate.of(2026, 11, 15).toDate()
        override fun invoke() = now
    }
    private val end = LocalDate.of(2026, 11, 24).toDate()

    @Before fun prepare() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Context>()
        context = object : ContextWrapper(app) {
            override fun getApplicationContext(): Context = this
            override fun getFilesDir(): File = File(app.cacheDir, "quick-repository-tests").also { it.mkdirs() }
        }
        context.budgetDataStore.edit { it.clear() }
        db = Room.inMemoryDatabaseBuilder(context, DatabaseModule::class.java).allowMainThreadQueries().build()
        repository = SpendsRepository(context, db.transactionDao(), db.categoryDao(), clock)
        repository.setBudget(BigDecimal("1000.00"), end)
    }

    @After fun close() { db.close() }

    private fun expense(amount: String, category: Category? = null, comment: String = "") = Transaction(
        TransactionType.SPENT, BigDecimal(amount), clock.now, comment,
        categoryId = category?.id,
    )

    @Test fun categoryAndCommentAreStoredIndependently() = runBlocking {
        val food = repository.addCategory("Продукты")
        val first = expense("30.00", food, "На выходные")
        val second = expense("20.00", food, "Онлайн")
        repository.addSpent(first)
        repository.addSpent(second)
        val saved = db.transactionDao().getById(first.uid)!!
        assertEquals(food.id, saved.categoryId)
        assertEquals("На выходные", saved.comment)
        assertEquals(2, db.transactionDao().getForPeriod(saved.periodId).count { it.categoryId == food.id })
        assertEquals(BigDecimal("950.00"), repository.howMuchBudgetRest())
    }

    @Test fun amountAloneIsEnoughAndOverdraftStaysNegative() = runBlocking {
        val transaction = expense("150.00")
        repository.addSpent(transaction)
        assertNull(db.transactionDao().getById(transaction.uid)!!.categoryId)
        assertEquals(BigDecimal("-50.00"), repository.getDailyBudget().first() - repository.getSpentFromDailyBudget().first())
        assertEquals(BigDecimal("850.00"), repository.howMuchBudgetRest())
        repository.removeSpent(transaction)
        assertEquals(BigDecimal("1000.00"), repository.howMuchBudgetRest())
    }

    @Test fun editingMetadataPreservesIdentityAndBudget() = runBlocking {
        val transaction = expense("150.00")
        repository.addSpent(transaction)
        val original = db.transactionDao().getById(transaction.uid)!!
        val food = repository.addCategory("Продукты")
        repository.editSpent(original, original.copy(categoryId = food.id, comment = "Магазин"))
        val updated = db.transactionDao().getById(original.uid)!!
        assertEquals(food.id, updated.categoryId)
        assertEquals("Магазин", updated.comment)
        assertEquals(BigDecimal("850.00"), repository.howMuchBudgetRest())
        assertEquals(BigDecimal("150.00"), repository.getSpentFromDailyBudget().first())
    }

    @Test fun newPeriodKeepsOldRecordsAndCategories() = runBlocking {
        val food = repository.addCategory("Продукты")
        val transaction = expense("50.00", food)
        repository.addSpent(transaction)
        val oldPeriod = db.transactionDao().getById(transaction.uid)!!.periodId
        repository.setBudget(BigDecimal("2000.00"), end)
        val newPeriod = context.budgetDataStore.data.first()[periodIdStoreKey]!!
        assertNotEquals(oldPeriod, newPeriod)
        assertNotNull(db.transactionDao().getById(transaction.uid))
        assertTrue(db.transactionDao().getForPeriod(newPeriod).none { it.type == TransactionType.SPENT })
        assertEquals(BigDecimal("2000.00"), repository.howMuchBudgetRest())
        val nextExpense = expense("10.00", food)
        repository.addSpent(nextExpense)
        assertEquals(food.id, db.transactionDao().getById(nextExpense.uid)!!.categoryId)
    }

    @Test fun remainderIsRedistributedAcrossRemainingDays() = runBlocking {
        repository.addSpent(expense("150.00"))
        clock.now = clock.now.toLocalDate().plusDays(1).toDate()
        repository.setDailyBudget(repository.whatBudgetForDay(applyTodaySpends = true))
        assertEquals(BigDecimal("94.44"), repository.getDailyBudget().first())
        assertEquals(0, repository.getSpentFromDailyBudget().first().signum())
        assertEquals(BigDecimal("850.00"), repository.howMuchBudgetRest())
    }
}
