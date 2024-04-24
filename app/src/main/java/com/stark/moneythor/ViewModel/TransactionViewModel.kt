package com.stark.moneythor.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.stark.moneythor.Database.TransactionDatabase
import com.stark.moneythor.Model.Transaction
import com.stark.moneythor.Repository.TransactionRepository


class TransactionViewModel(application: Application): AndroidViewModel(application) {

    val repository: TransactionRepository

    init{
        val dao = TransactionDatabase.getDatabaseInstance(application).myTransactionDao()
        repository= TransactionRepository(dao)
    }

    fun addTransaction(transaction: Transaction){
        repository.insertTransaction(transaction)
    }

    fun getTransaction(): LiveData<List<Transaction>> = repository.getAllTransaction()

    fun getMonthlyTransaction(month:Int,Year:Int): LiveData<List<Transaction>> = repository.getMonthlyTransaction(month,Year)

    fun getYearlyTransaction(year: Int): LiveData<List<Transaction>> = repository.getYearlyTransaction(year)

    fun deleteTransaction(id:Int){
        repository.deleteTransaction(id)
    }

    fun updateTransaction(transaction: Transaction){
        Log.d("ViewModel", "Updating transaction: $transaction")
        repository.updateTransaction(transaction)
        Log.d("ViewModel", "Transaction updated successfully")

    }

    fun getTransactionsInDateRange(startDate: String, endDate: String): LiveData<List<Transaction>> {
        return repository.getTransactionsInDateRange(startDate, endDate)
    }



}