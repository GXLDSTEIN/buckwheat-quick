package com.danilkinkin.buckwheat.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.danilkinkin.buckwheat.data.dao.TransactionDao
import com.danilkinkin.buckwheat.data.entities.Transaction
import com.danilkinkin.buckwheat.data.entities.TransactionType

class FakeTransactionDao : TransactionDao {
    private val spends = mutableListOf<Transaction>()
    private val live = MutableLiveData<List<Transaction>>(emptyList())
    private var nextId = 1
    override fun getAll(): LiveData<List<Transaction>> = live
    override fun getAll(type: TransactionType) = live.map { list -> list.filter { it.type == type } }
    override fun getForPeriod(periodId: String) = spends.filter { it.periodId == periodId }
    override fun getById(uid: Int) = spends.find { it.uid == uid }
    override fun insert(vararg transaction: Transaction): List<Long> {
        val ids = transaction.map {
            if (it.uid == 0) it.uid = nextId++
            spends.add(it)
            it.uid.toLong()
        }
        live.value = spends.toList()
        return ids
    }
    override fun update(vararg transaction: Transaction) {
        transaction.forEach { updated ->
            val index = spends.indexOfFirst { it.uid == updated.uid }
            if (index >= 0) spends[index] = updated
        }
        live.value = spends.toList()
    }
    override fun deleteById(uid: Int) {
        spends.removeAll { it.uid == uid }
        live.value = spends.toList()
    }
    override fun deleteAll() { spends.clear(); live.value = emptyList() }
}
