package com.example.spendease.TransactionDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spendease.Dao.TransactionDao
import com.example.spendease.TransactionData

@Database(entities = [TransactionData::class], version = 1, exportSchema = false )
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun getTransactionDao(): TransactionDao

    companion object{
        // Singleton prevents multiple instances of database opening at the same time.
        private var INSTANCE:TransactionDatabase? = null
        fun getDatabaseInstance(context: Context):TransactionDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this)
            {
                val roomDatabaseInstance = Room.databaseBuilder(context,TransactionDatabase::class.java,"Transaction")
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = roomDatabaseInstance
                return roomDatabaseInstance
            }
        }
    }
}