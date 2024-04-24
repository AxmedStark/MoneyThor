package com.stark.moneythor.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stark.moneythor.Dao.TransactionDao
import com.stark.moneythor.Model.Transaction

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun myTransactionDao(): TransactionDao

    companion object {
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabaseInstance(context: Context): TransactionDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val roomDatabaseInstance = Room.databaseBuilder(
                    context,
                    TransactionDatabase::class.java,
                    "Transaction"
                )
                    .allowMainThreadQueries()
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // Add this line for query logging
                    .build()
                INSTANCE = roomDatabaseInstance
                return roomDatabaseInstance
            }
        }
    }
}
