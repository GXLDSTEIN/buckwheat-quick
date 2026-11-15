package com.danilkinkin.buckwheat.di

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.danilkinkin.buckwheat.data.dao.CategoryDao
import com.danilkinkin.buckwheat.data.dao.StorageDao
import com.danilkinkin.buckwheat.data.dao.TransactionDao
import com.danilkinkin.buckwheat.data.entities.Category
import com.danilkinkin.buckwheat.data.entities.Storage
import com.danilkinkin.buckwheat.data.entities.Transaction

// A separate application and database: no import of the upstream installation.
@Database(entities = [Transaction::class, Storage::class, Category::class], version = 1, exportSchema = true)
@TypeConverters(RoomConverters::class)
abstract class DatabaseModule : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun storageDao(): StorageDao
    abstract fun categoryDao(): CategoryDao
}
