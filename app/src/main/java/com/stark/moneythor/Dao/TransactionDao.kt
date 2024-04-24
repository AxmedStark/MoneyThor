package com.stark.moneythor.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.stark.moneythor.Model.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `Transaction` ORDER BY year DESC,month DESC,day DESC,category DESC")
    fun getTransaction(): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE month=:month AND year=:year")
    fun getMonthlyTransaction(month: Int,year: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE year=:year")
    fun getYearlyTransaction(year: Int): LiveData<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM `TRANSACTION` WHERE id=:id")
    fun deleteTransaction(id: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM 'Transaction' WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsInDateRange(startDate: String, endDate: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE type=:transactionType ORDER BY year DESC, month DESC, day DESC, category DESC")
    fun getTransactionsByType(transactionType: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE month=:month AND year=:year AND type=:transactionType")
    fun getMonthlyTransactionsByType(month: Int, year: Int, transactionType: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE year=:year AND type=:transactionType")
    fun getYearlyTransactionsByType(year: Int, transactionType: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE date BETWEEN :startDate AND :endDate AND type=:transactionType")
    fun getTransactionsInDateRangeByType(startDate: Long, endDate: Long, transactionType: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM 'Transaction' WHERE type = 'Income'")
    fun getIncomeTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM 'Transaction' WHERE type = 'Expense'")
    fun getExpenseTransactions(): LiveData<List<Transaction>>

}