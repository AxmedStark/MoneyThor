package com.stark.moneythor.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stark.moneythor.Repository.TransactionRepository
import com.stark.moneythor.Model.Transaction

class StatisticsViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    // LiveData for observing income and expense
    private val _income = MutableLiveData<Double>()
    val income: LiveData<Double> get() = _income

    private val _expense = MutableLiveData<Double>()
    val expense: LiveData<Double> get() = _expense

    // Method to fetch and calculate income
    fun calculateIncome() {
        // You need to implement logic to fetch income data from the repository
        val incomeList: LiveData<List<Transaction>> = transactionRepository.getIncomeTransactions()

        // Observe incomeList and calculate total income when data changes
        incomeList.observeForever { transactions ->
            val totalIncome = transactions.sumByDouble { it.amount }
            _income.postValue(totalIncome)
        }
    }

    // Method to fetch and calculate expense
    fun calculateExpense() {
        // You need to implement logic to fetch expense data from the repository
        val expenseList: LiveData<List<Transaction>> = transactionRepository.getExpenseTransactions()

        // Observe expenseList and calculate total expense when data changes
        expenseList.observeForever { transactions ->
            val totalExpense = transactions.sumByDouble { it.amount }
            _expense.postValue(totalExpense)
        }
    }

    // LiveData for observing income transactions
    fun getIncomeTransactions(): LiveData<List<Transaction>> {
        return transactionRepository.getIncomeTransactions()
    }

    // LiveData for observing expense transactions
    fun getExpenseTransactions(): LiveData<List<Transaction>> {
        return transactionRepository.getExpenseTransactions()
    }
}
