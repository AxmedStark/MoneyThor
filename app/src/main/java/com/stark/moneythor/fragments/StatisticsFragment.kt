package com.stark.moneythor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stark.moneythor.R
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var periodSpinner: Spinner
    private lateinit var totalIncomeTextView: TextView
    private lateinit var totalExpenseTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        periodSpinner = view.findViewById(R.id.statisticsPeriodSpinner)
        totalIncomeTextView = view.findViewById(R.id.totalIncomeTextView)
        totalExpenseTextView = view.findViewById(R.id.totalExpenseTextView)

        val periodsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.statistics_periods,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        periodSpinner.adapter = periodsAdapter

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateStatistics(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Default to the first item in the spinner
        updateStatistics(0)

        return view
    }

    private fun updateStatistics(selectedPeriod: Int) {
        // Get the start and end dates based on the selected period
        val (startDate, endDate) = when (selectedPeriod) {
            0 -> getLastMonthDates()
            1 -> getLastYearDates()
            else -> getLastMonthDates() // Default to last month
        }

        // Call a function to fetch and calculate statistics based on the date range
        val (totalIncome, totalExpense) = calculateStatistics(startDate, endDate)

//        totalIncomeTextView.text = "Total Income: $$totalIncome"
//        totalExpenseTextView.text = "Total Expense: $$totalExpense"
    }



    private fun getLastMonthDates(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1) // Move to last month
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to first day of the month
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1) // Move to this month
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Set to last day of the last month
        val endDate = calendar.timeInMillis

        return Pair(startDate, endDate)
    }

    private fun getLastYearDates(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1) // Move to last year
        calendar.set(Calendar.MONTH, Calendar.JANUARY) // Set to first month of the year
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to first day of the month
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.YEAR, 1) // Move to this year
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Set to last day of the last month
        val endDate = calendar.timeInMillis

        return Pair(startDate, endDate)
    }

    // Define a hypothetical Transaction data class
    data class Transaction(
        val id: String,
        val date: Long,
        val type: String, // "Income" or "Expense"
        val amount: Double
    )


    private fun calculateStatistics(startDate: Long, endDate: Long): Pair<Double, Double> {
        // TODO: Fetch transactions from your data source
        // For this example, creating a list of hypothetical transactions
        val transactions = listOf(
            Transaction("1", System.currentTimeMillis() - 86400000, "Expense", 50.0),
            Transaction("2", System.currentTimeMillis() - 86400000 * 2, "Income", 100.0),
            Transaction("3", System.currentTimeMillis() - 86400000 * 10, "Expense", 30.0),
            // Add more transactions as needed
        )

        var totalIncome = 0.0
        var totalExpense = 0.0

        for (transaction in transactions) {
            if (transaction.date in startDate..endDate) {
                if (transaction.type == "Income") {
                    totalIncome += transaction.amount
                } else if (transaction.type == "Expense") {
                    totalExpense += transaction.amount
                }
            }
        }

        return Pair(totalIncome, totalExpense)
    }


}
