package com.danilkinkin.buckwheat.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.danilkinkin.buckwheat.data.entities.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY rowid")
    fun getAll(): LiveData<List<Category>>

    @Insert
    fun insert(category: Category)
}
