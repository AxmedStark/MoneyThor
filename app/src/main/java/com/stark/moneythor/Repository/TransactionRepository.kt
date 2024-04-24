package com.stark.moneythor.Repository

import androidx.lifecycle.LiveData
import com.stark.moneythor.Dao.TransactionDao
import com.stark.moneythor.Model.Transaction

class TransactionRepository(val dao: TransactionDao) {

    fun getAllTransaction(): LiveData<List<Transaction>> {
        return dao.getTransaction()
    }

    fun getMonthlyTransaction(month:Int,Year:Int): LiveData<List<Transaction>>{
        return dao.getMonthlyTransaction(month,Year)
    }

    fun getYearlyTransaction(year:Int): LiveData<List<Transaction>>{
        return dao.getYearlyTransaction(year)
    }

    fun insertTransaction(transaction: Transaction){
        dao.insertTransaction(transaction)
    }

    fun deleteTransaction(id:Int){
        dao.deleteTransaction(id)
    }

    fun updateTransaction(transaction: Transaction){
        dao.updateTransaction(transaction)
    }

    fun getTransactionsInDateRange(startDate: String, endDate: String): LiveData<List<Transaction>> {
        return dao.getTransactionsInDateRange(startDate, endDate)
    }

    fun getIncomeTransactions(): LiveData<List<Transaction>> {
        return dao.getIncomeTransactions()
    }

    fun getExpenseTransactions(): LiveData<List<Transaction>> {
        return dao.getExpenseTransactions()
    }
}