package com.danilkinkin.buckwheat.di

import androidx.lifecycle.MutableLiveData
import com.danilkinkin.buckwheat.data.dao.CategoryDao
import com.danilkinkin.buckwheat.data.entities.Category

class FakeCategoryDao : CategoryDao {
    private val categories = MutableLiveData<List<Category>>(emptyList())
    override fun getAll() = categories
    override fun insert(category: Category) {
        categories.value = categories.value.orEmpty() + category
    }
}
