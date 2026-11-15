package com.danilkinkin.buckwheat.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.danilkinkin.buckwheat.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app.applicationContext,
        DatabaseModule::class.java,
        "buckwheat-quick-db",
    )
        .allowMainThreadQueries()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                val names = app.resources.getStringArray(R.array.quick_default_categories)
                names.forEachIndexed { index, name ->
                    db.execSQL("INSERT INTO categories (id, name) VALUES (?, ?)", arrayOf("default-$index", name))
                }
            }
        })
        .build()

    @Singleton
    @Provides
    fun provideTransactionDao(db: DatabaseModule) = db.transactionDao()

    @Singleton
    @Provides
    fun provideStorageDao(db: DatabaseModule) = db.storageDao()

    @Singleton
    @Provides
    fun provideCategoryDao(db: DatabaseModule) = db.categoryDao()
}