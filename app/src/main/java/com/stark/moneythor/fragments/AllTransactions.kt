package com.stark.moneythor.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stark.moneythor.Adapter.TransactionAdapter
import com.stark.moneythor.R
import com.stark.moneythor.ViewModel.TransactionViewModel
import com.stark.moneythor.databinding.FragmentAllTransactionsBinding
import com.google.android.material.button.MaterialButton
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class AllTransactions : Fragment() ,View.OnClickListener {


    lateinit var binding:FragmentAllTransactionsBinding

    private val viewModel: TransactionViewModel by viewModels()
    lateinit var mPieChart:PieChart
    lateinit var mPieChart2:PieChart
    private var month =""
    private var year=0
    private var monthInt =1
    private var totalExpense = 0.0
    private var totalIncome = 0.0
    private var totalGoal = 5000.0f
    private var totalFood = 0.0f
    private var totalShopping = 0.0f
    private var totalTransport=0.0f
    private var totalHealth = 0.0f
    private var totalOthers = 0.0f
    private var totalAcademics = 0.0f
    private var totalGoalIncome = 5000.0f
    private var totalFoodIncome = 0.0f
    private var totalShoppingIncome = 0.0f
    private var totalTransportIncome=0.0f
    private var totalHealthIncome = 0.0f
    private var totalOthersIncome = 0.0f
    private var totalAcademicsIncome = 0.0f
    lateinit var userDetails:SharedPreferences
    private var startDate: Long? = null
    private var endDate: Long? = null
//    private var formattedStartDate = null
//    private var formattedEndDate = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        getActivity()?.getWindow()?.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.background))
        binding = FragmentAllTransactionsBinding.inflate(inflater, container, false)
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE
        userDetails = requireActivity().getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
        setListener()

        when (binding.toggleSelector.checkedButtonId) {
            R.id.all -> showAllTransactions()
            R.id.monthly -> showMonthlyTransactions()
            R.id.yearly -> showYearlyTransactions()
        }

        binding.toggleSelector.addOnButtonCheckedListener { toggleSelector, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.all -> showAllTransactions()
                    R.id.monthly -> showMonthlyTransactions()
                    R.id.yearly -> showYearlyTransactions()
                }
            }
        }
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
            )
        return binding.root
    }



    private fun showDatePicker(isStartDate: Boolean) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker: MaterialDatePicker<Long> = builder.build()

        picker.addOnPositiveButtonClickListener { selectedDate ->
            if (isStartDate) {
                startDate = selectedDate
                binding.selectStartDateButton.text =
                    SimpleDateFormat("dd MMMM yyyy", Locale.US).format(selectedDate)
            } else {
                endDate = selectedDate
                binding.selectEndDateButton.text  =
                    SimpleDateFormat("dd MMMM yyyy", Locale.US).format(selectedDate)
            }
            Log.d("DatePicker", "applyDateFilter is being called")
            val startDateFormatted = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date(startDate!!))
            val endDateFormatted = if (endDate != null) SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date(endDate!!)) else "null"
            Log.d("DatePicker", " Start Date: $startDate, End Date: $endDate")
            Log.d("DatePicker", "Selected Start Date: $startDateFormatted, End Date: $endDateFormatted")


            applyDateFilter()
        }

        picker.show(parentFragmentManager, picker.toString())
    }

    private fun applyDateFilter() {
        if (startDate != null && endDate != null) {

            try {
                val formattedStartDate = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date(startDate!!))
                val formattedEndDate = if (endDate != null) SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date(endDate!!)) else "null"

                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
//                val formattedStartDate = dateFormat.format(Date(startDate!!))
//                val formattedEndDate = if (endDate != null) dateFormat.format(Date(endDate!!)) else "null"
                Log.d("DatePicker", " Start Date: $startDate, End Date: $endDate")
                Log.d("DatePicker", "Selected Start Date: $formattedStartDate, End Date: $formattedEndDate")

                viewModel.getTransactionsInDateRange(formattedStartDate, formattedEndDate)
                    .observe(viewLifecycleOwner) { transactionList ->
                        Log.d("DatePicker", "Observer triggered with ${transactionList.size} transactions. Start Date: $formattedStartDate, End Date: $formattedEndDate")
                        for (transaction in transactionList) {
                            Log.d("DatePicker", "Transaction ID: ${transaction.id}, Date: ${transaction.date}, Amount: ${transaction.amount}, Category: ${transaction.category}, Type: ${transaction.type}")
                        }
                        if (transactionList.isEmpty()) {
                            // Handle case when no transactions are found in the selected date range
                            binding.noTransactionsDoneText.text = "No transactions found in the selected date range"
                            binding.noTransactionsDoneText.visibility = View.VISIBLE
                            binding.monthlyCard.visibility = View.GONE
                            binding.transactionRecyclerView.visibility = View.GONE
                            binding.text1.visibility = View.GONE
                        } else {
                            binding.noTransactionsDoneText.visibility = View.GONE
                            binding.transactionRecyclerView.visibility = View.VISIBLE
                            binding.text1.visibility = View.VISIBLE
                            binding.transactionRecyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                            binding.transactionRecyclerView.adapter = TransactionAdapter(
                                requireContext(),
                                requireActivity(),
                                "AllTransactions",
                                transactionList.reversed()
                            )

                            // Calculate the totals based on the filtered list
                            var totalExpense = 0.0
                            var totalFood = 0.0f
                            var totalShopping = 0.0f
                            var totalTransport = 0.0f
                            var totalHealth = 0.0f
                            var totalOthers = 0.0f
                            var totalAcademics = 0.0f

                            var totalIncome = 0.0
                            var totalFoodIncome = 0.0f
                            var totalShoppingIncome = 0.0f
                            var totalTransportIncome = 0.0f
                            var totalHealthIncome = 0.0f
                            var totalOthersIncome = 0.0f
                            var totalAcademicsIncome = 0.0f

                            for (i in transactionList) {
                                if (i.type.equals("Expence")){
                                    totalExpense += i.amount
                                    when (i.category) {
                                        "Food" -> totalFood += i.amount.toFloat()
                                        "Shopping" -> totalShopping += i.amount.toFloat()
                                        "Transport" -> totalTransport += i.amount.toFloat()
                                        "Health" -> totalHealth += i.amount.toFloat()
                                        "Other" -> totalOthers += i.amount.toFloat()
                                        "Education" -> totalAcademics += i.amount.toFloat()
                                    }
                                }

                                if (i.type.equals("Income")){
                                    totalIncome += i.amount
                                    when (i.category) {
                                        "Food" -> totalFoodIncome += i.amount.toFloat()
                                        "Shopping" -> totalShoppingIncome += i.amount.toFloat()
                                        "Transport" -> totalTransportIncome += i.amount.toFloat()
                                        "Health" -> totalHealthIncome += i.amount.toFloat()
                                        "Other" -> totalOthersIncome += i.amount.toFloat()
                                        "Education" -> totalAcademicsIncome += i.amount.toFloat()
                                    }
                                }
                            }


                            // Update the UI with the new totals
                            binding.expense.text = "$${totalExpense.toInt()}"
                            binding.income.text = "$${totalIncome.toInt()}"
                            binding.budget.text = "" // You may need to adjust this based on your logic
                            binding.date.text = "Selected Date Range"
//                            binding.indicator.setImageResource(R.drawable.ic_positive_amount) // Assuming positive by default

                            showPiChart() // You may need to update this function based on the filtered data
                        }
                    }
            } catch (e: Exception) {
                Log.e("DatePicker", "Error fetching transactions: ${e.message}")
                // Handle the error appropriately
            }

        } else {
            // Handle the case when startDate or endDate is null
            // You might want to display a message to the user or take appropriate action
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAllTransactions() {
        binding.transactionRecyclerView.visibility = View.VISIBLE
        binding.selectors.visibility = View.GONE
        binding.monthlyCard.visibility = View.GONE
        binding.yearSpinner.visibility = View.GONE
        binding.text1.visibility = View.GONE
        binding.title.text = "All Transactions"
        viewModel.getTransaction().observe(viewLifecycleOwner) { transactionList ->
            for (transaction in transactionList) {
                Log.d("DatePicker", "Transaction ID: ${transaction.id}, Date: ${transaction.date}, Amount: ${transaction.amount}, Category: ${transaction.category}, Type: ${transaction.type}")
            }
            if (transactionList.isEmpty()) {
                binding.noTransactionsDoneText.text = "No transaction done Yet"
                binding.noTransactionsDoneText.visibility = View.VISIBLE
                binding.transactionRecyclerView.visibility = View.GONE
            } else {

                binding.noTransactionsDoneText.visibility = View.GONE
                binding.transactionRecyclerView.visibility = View.VISIBLE
                binding.transactionRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                binding.transactionRecyclerView.adapter =
                    TransactionAdapter(
                        requireContext(),
                        requireActivity(),
                        "AllTransactions",
                        transactionList
                    )
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun showMonthlyTransactions() {
        binding.text.text = "Monthly Budget"
        year=SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()).toInt()
        val list = mutableListOf(2020)
        list.clear()
        for(i in year downTo 2020){
            list += i
        }
        val yearAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,list)
        binding.yearSpinner.setAdapter(yearAdapter)
        setMonth(binding.January,binding.January)
        showMonthsTransaction()
        binding.transactionRecyclerView.visibility = View.VISIBLE
        binding.selectors.visibility = View.VISIBLE
        binding.monthlyCard.visibility = View.VISIBLE
        binding.yearSpinner.visibility = View.VISIBLE
        binding.text1.visibility = View.VISIBLE
        binding.title.text = "Monthly Transactions"
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                year=binding.yearSpinner.selectedItem.toString().toInt()
                showMonthsTransaction()
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
                year=binding.yearSpinner.selectedItem.toString().toInt()
                showMonthsTransaction()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showMonthsTransaction(){
        mPieChart=binding.piechart
        mPieChart2=binding.piechart2
        mPieChart.clearChart()
        mPieChart2.clearChart()
        totalExpense = 0.0
        totalIncome = 0.0
        totalGoal = userDetails.getString("MonthlyBudget","0")?.toFloat()!!
        totalFood = 0.0f
        totalShopping = 0.0f
        totalTransport=0.0f
        totalHealth = 0.0f
        totalOthers = 0.0f
        totalAcademics = 0.0f
        totalGoalIncome = userDetails.getString("MonthlyBudget","0")?.toFloat()!!
        totalFoodIncome = 0.0f
        totalShoppingIncome = 0.0f
        totalTransportIncome=0.0f
        totalHealthIncome = 0.0f
        totalOthersIncome = 0.0f
        totalAcademicsIncome = 0.0f
        viewModel.getMonthlyTransaction(monthInt, year).observe(viewLifecycleOwner
        ) { transactionList ->
            if (transactionList.isEmpty()) {
                binding.noTransactionsDoneText.text = "No transaction done on $month $year "
                binding.noTransactionsDoneText.visibility = View.VISIBLE
                binding.monthlyCard.visibility = View.GONE
                binding.transactionRecyclerView.visibility = View.GONE
                binding.text1.visibility = View.GONE
            } else {
                for (transaction in transactionList) {
                    Log.d("DatePicker", "Transaction Date: ${transaction.date}")
                }
                binding.monthlyCard.visibility = View.VISIBLE
                binding.noTransactionsDoneText.visibility = View.GONE
                binding.transactionRecyclerView.visibility = View.VISIBLE
                binding.text1.visibility = View.VISIBLE
                binding.transactionRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                binding.transactionRecyclerView.adapter = TransactionAdapter(
                    requireContext(),
                    requireActivity(),
                    "AllTransactions",
                    transactionList.reversed()
                )

                for (i in transactionList) {
                    if (i.type == "Expense") {
                        totalExpense += i.amount
                        when (i.category) {
                            "Food" -> {
                                totalFood += (i.amount.toFloat())
                            }
                            "Shopping" -> {
                                totalShopping += (i.amount.toFloat())
                            }
                            "Transport" -> {
                                totalTransport += (i.amount.toFloat())
                            }
                            "Health" -> {
                                totalHealth += (i.amount.toFloat())
                            }
                            "Other" -> {
                                totalOthers += (i.amount.toFloat())
                            }
                            "Education" -> {
                                totalAcademics += (i.amount.toFloat())
                            }
                        }
                    }
                    if (i.type == "Income") {
                        totalIncome += i.amount
                        when (i.category) {
                            "Food" -> {
                                totalFoodIncome += (i.amount.toFloat())
                            }
                            "Shopping" -> {
                                totalShoppingIncome += (i.amount.toFloat())
                            }
                            "Transport" -> {
                                totalTransportIncome += (i.amount.toFloat())
                            }
                            "Health" -> {
                                totalHealthIncome += (i.amount.toFloat())
                            }
                            "Other" -> {
                                totalOthersIncome += (i.amount.toFloat())
                            }
                            "Education" -> {
                                totalAcademicsIncome += (i.amount.toFloat())
                            }
                        }
                    }
                }


                binding.expense.text = "$${totalExpense.toInt()}"
                binding.income.text = "$${totalIncome.toInt()}"
                binding.budget.text = "$${totalGoal.toInt()}"
                binding.date.text = "${month} ${year}"
//                if (totalExpense > totalGoal) {
//                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
//                    binding.expense.setTextColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                } else {
//                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
//                }
                showPiChart()
            }
        }
    }


    private fun showPiChart() {
        mPieChart.addPieSlice(PieModel("Food", totalFood, ContextCompat.getColor(requireContext(), R.color.yellow)))
        mPieChart.addPieSlice(PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue)))
        mPieChart.addPieSlice(PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.red)))
        mPieChart.addPieSlice(PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.brown)))
        mPieChart.addPieSlice(PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.violet)))
        mPieChart.addPieSlice(PieModel("Academics", totalAcademics, ContextCompat.getColor(requireContext(), R.color.green)))

        mPieChart2.addPieSlice(PieModel("Food", totalFoodIncome, ContextCompat.getColor(requireContext(), R.color.yellow)))
        mPieChart2.addPieSlice(PieModel("Shopping", totalShoppingIncome, ContextCompat.getColor(requireContext(), R.color.blue)))
        mPieChart2.addPieSlice(PieModel("Health", totalHealthIncome, ContextCompat.getColor(requireContext(), R.color.red)))
        mPieChart2.addPieSlice(PieModel("Others", totalOthersIncome, ContextCompat.getColor(requireContext(), R.color.brown)))
        mPieChart2.addPieSlice(PieModel("Transport", totalTransportIncome, ContextCompat.getColor(requireContext(), R.color.violet)))
        mPieChart2.addPieSlice(PieModel("Academics", totalAcademicsIncome, ContextCompat.getColor(requireContext(), R.color.green)))

//        if (totalGoal>totalExpense){
//            mPieChart.addPieSlice(PieModel("Empty",totalGoal-(totalExpense.toFloat()) , ContextCompat.getColor(requireContext(), R.color.background_deep)))
//        }
//        if (totalGoalIncome>totalIncome){
//            mPieChart2.addPieSlice(PieModel("Empty",totalGoalIncome-(totalIncome.toFloat()) , ContextCompat.getColor(requireContext(), R.color.background_deep)))
//        }

        mPieChart.startAnimation()
        mPieChart2.startAnimation()
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showYearlyTransactions() {
        binding.title.text = "Yearly Transactions"
        binding.text.text = "Yearly Budget"
        year=SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()).toInt()
        val list = mutableListOf(2020)
        list.clear()
        for(i in year downTo 2020){
            list += i
        }
        val yearAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,list)
        binding.yearSpinner.setAdapter(yearAdapter)
        binding.transactionRecyclerView.visibility = View.VISIBLE
        binding.selectors.visibility = View.GONE
        binding.monthlyCard.visibility = View.VISIBLE
        binding.yearSpinner.visibility = View.VISIBLE
        binding.text1.visibility = View.VISIBLE
        showYearlyTransaction()
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                year=binding.yearSpinner.selectedItem.toString().toInt()
                showYearlyTransaction()
            } // to close the onItemSelected
            override fun onNothingSelected(parent: AdapterView<*>) {
                year=binding.yearSpinner.selectedItem.toString().toInt()
                showYearlyTransaction()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showYearlyTransaction(){
        mPieChart=binding.piechart
        mPieChart.clearChart()
        totalExpense = 0.0
        totalGoal = userDetails.getString("YearlyBudget","0")?.toFloat()!!
        totalFood = 0.0f
        totalShopping = 0.0f
        totalTransport=0.0f
        totalHealth = 0.0f
        totalOthers = 0.0f
        totalAcademics = 0.0f
        mPieChart2=binding.piechart2
        mPieChart2.clearChart()
        totalIncome = 0.0
        totalGoalIncome = userDetails.getString("YearlyBudget","0")?.toFloat()!!
        totalFoodIncome = 0.0f
        totalShoppingIncome = 0.0f
        totalTransportIncome=0.0f
        totalHealthIncome = 0.0f
        totalOthersIncome = 0.0f
        totalAcademicsIncome = 0.0f
        viewModel.getYearlyTransaction(year).observe(viewLifecycleOwner) { transactionList ->
            if (transactionList.isEmpty()) {
                binding.noTransactionsDoneText.text = "No transaction done on Year $year "
                binding.noTransactionsDoneText.visibility = View.VISIBLE
                binding.monthlyCard.visibility = View.GONE
                binding.transactionRecyclerView.visibility = View.GONE
                binding.text1.visibility = View.GONE
            } else {
                binding.monthlyCard.visibility = View.VISIBLE
                binding.noTransactionsDoneText.visibility = View.GONE
                binding.transactionRecyclerView.visibility = View.VISIBLE
                binding.text1.visibility = View.VISIBLE
                binding.transactionRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                binding.transactionRecyclerView.adapter = TransactionAdapter(
                    requireContext(),
                    requireActivity(),
                    "AllTransactions",
                    transactionList.reversed()
                )

                for (i in transactionList) {
                    if (i.type == "Expense") {
                        totalExpense += i.amount
                        when (i.category) {
                            "Food" -> {
                                totalFood += (i.amount.toFloat())
                            }
                            "Shopping" -> {
                                totalShopping += (i.amount.toFloat())
                            }
                            "Transport" -> {
                                totalTransport += (i.amount.toFloat())
                            }
                            "Health" -> {
                                totalHealth += (i.amount.toFloat())
                            }
                            "Other" -> {
                                totalOthers += (i.amount.toFloat())
                            }
                            "Education" -> {
                                totalAcademics += (i.amount.toFloat())
                            }
                        }
                    }
                    if (i.type == "Income") {
                        totalIncome += i.amount
                        when (i.category) {
                            "Food" -> {
                                totalFoodIncome += (i.amount.toFloat())
                            }
                            "Shopping" -> {
                                totalShoppingIncome += (i.amount.toFloat())
                            }
                            "Transport" -> {
                                totalTransportIncome += (i.amount.toFloat())
                            }
                            "Health" -> {
                                totalHealthIncome += (i.amount.toFloat())
                            }
                            "Other" -> {
                                totalOthersIncome += (i.amount.toFloat())
                            }
                            "Education" -> {
                                totalAcademicsIncome += (i.amount.toFloat())
                            }
                        }
                    }
                }

                binding.expense.text = "$${totalExpense.toInt()}"
                binding.income.text = "$${totalIncome.toInt()}"
                binding.budget.text = "$${totalGoal.toInt()}"
                binding.date.text = "Year: ${year}"
//                if (totalExpense > totalGoal) {
//                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
//                    binding.expense.setTextColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                } else {
//                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
//                }
                showPiChart()
            }
        }
    }


    private fun setListener() {
        binding.selectStartDateButton.setOnClickListener(this)
        binding.selectEndDateButton.setOnClickListener(this)
        binding.January.setOnClickListener(this)
        binding.February.setOnClickListener(this)
        binding.March.setOnClickListener(this)
        binding.April.setOnClickListener(this)
        binding.May.setOnClickListener(this)
        binding.June.setOnClickListener(this)
        binding.July.setOnClickListener(this)
        binding.August.setOnClickListener(this)
        binding.September.setOnClickListener(this)
        binding.October.setOnClickListener(this)
        binding.November.setOnClickListener(this)
        binding.December.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.selectStartDateButton -> {
                showDatePicker(true)
//                applyDateFilter()
            }
            binding.selectEndDateButton -> {
                showDatePicker(false)
//                applyDateFilter()
            }
            binding.January -> {
                setMonth(v,binding.January)
                monthInt=1
                showMonthsTransaction()
            }
            binding.February -> {
                setMonth(v,binding.February)
                monthInt=2
                showMonthsTransaction()
            }
            binding.March -> {
                setMonth(v,binding.March)
                monthInt=3
                showMonthsTransaction()
            }
            binding.April -> {
                setMonth(v,binding.April)
                monthInt=4
                showMonthsTransaction()
            }
            binding.May -> {
                setMonth(v,binding.May)
                monthInt=5
                showMonthsTransaction()
            }
            binding.June -> {
                setMonth(v,binding.June)
                monthInt=6
                showMonthsTransaction()
            }
            binding.July -> {
                setMonth(v,binding.July)
                monthInt=7
                showMonthsTransaction()
            }
            binding.August -> {
                setMonth(v,binding.August)
                monthInt=8
                showMonthsTransaction()
            }
            binding.September -> {
                setMonth(v,binding.September)
                monthInt=9
                showMonthsTransaction()
            }
            binding.October -> {
                setMonth(v,binding.October)
                monthInt=10
                showMonthsTransaction()
            }
            binding.November -> {
                setMonth(v,binding.November)
                monthInt=11
                showMonthsTransaction()
            }
            binding.December -> {
                setMonth(v,binding.December)
                monthInt=12
                showMonthsTransaction()
            }
        }
    }

    private fun setMonth(v: View, button: MaterialButton) {
        month = button.text.toString()
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_100))
        button.setStrokeColorResource(R.color.blue_100)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        when (v) {
            binding.January -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.February -> {
                removeBackground(binding.January)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.March -> {
                removeBackground(binding.February)
                removeBackground(binding.January)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.April -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.January)
                removeBackground(binding.May)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.May -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.January)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.June -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.January)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.July -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.January)
                removeBackground(binding.June)
                removeBackground(binding.January)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.August -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.January)
                removeBackground(binding.July)
                removeBackground(binding.January)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.September -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.January)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.January)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.October -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.January)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.January)
                removeBackground(binding.November)
                removeBackground(binding.December)
            }
            binding.November -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.January)
                removeBackground(binding.June)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.January)
                removeBackground(binding.December)
            }
            binding.December -> {
                removeBackground(binding.February)
                removeBackground(binding.March)
                removeBackground(binding.April)
                removeBackground(binding.May)
                removeBackground(binding.January)
                removeBackground(binding.July)
                removeBackground(binding.August)
                removeBackground(binding.September)
                removeBackground(binding.October)
                removeBackground(binding.November)
                removeBackground(binding.January)
            }
        }
    }

    private fun removeBackground(button: MaterialButton) {
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        button.setIconTintResource(R.color.textSecondary)
        button.setStrokeColorResource(R.color.textSecondary)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary))
    }


}



//import android.annotation.SuppressLint
//import android.app.DatePickerDialog
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.stark.moneythor.Adapter.TransactionAdapter
//import com.stark.moneythor.R
//import com.stark.moneythor.ViewModel.TransactionViewModel
//import com.stark.moneythor.databinding.FragmentAllTransactionsBinding
//import com.google.android.material.button.MaterialButton
//import org.eazegraph.lib.charts.PieChart
//import org.eazegraph.lib.models.PieModel
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.OnBackPressedCallback
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//class AllTransactions : Fragment() ,View.OnClickListener {
//
//
//    lateinit var binding:FragmentAllTransactionsBinding
//
//    private val viewModel: TransactionViewModel by viewModels()
//    lateinit var mPieChart:PieChart
//    private var month =""
//    private var year=0
//    private var monthInt =1
//    private var totalExpense = 0.0
//    private var totalGoal = 5000.0f
//    private var totalFood = 0.0f
//    private var totalShopping = 0.0f
//    private var totalTransport=0.0f
//    private var totalHealth = 0.0f
//    private var totalOthers = 0.0f
//    private var totalAcademics = 0.0f
//    lateinit var userDetails:SharedPreferences
//    private lateinit var startDateButton: Button
//    private lateinit var endDateButton: Button
//    private lateinit var filterButton: Button
//    private var startDate: Long = 0
//    private var endDate: Long = 0
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        startDateButton = view!!.findViewById(R.id.selectStartDateButton)
//        endDateButton = view!!.findViewById(R.id.selectEndDateButton)
//        filterButton = view!!.findViewById(R.id.applyDateFilterButton)
//        startDateButton.setOnClickListener {
//            showDatePicker(true)
//        }
//
//        endDateButton.setOnClickListener {
//            showDatePicker(false)
//        }
//
//        // Set click listener for the filter button
//        filterButton.setOnClickListener {
//            if (startDate > 0 && endDate > 0) {
//                filterByDateRange(startDate, endDate)
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Please select both start and end dates",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        // Inflate the layout for this fragment
//        getActivity()?.getWindow()?.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.background))
//        binding = FragmentAllTransactionsBinding.inflate(inflater, container, false)
//        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation)
//        bottomNav.visibility = View.VISIBLE
//        userDetails = requireActivity().getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
//        setListener()
//        when (binding.toggleSelector.checkedButtonId) {
//            R.id.all -> showAllTransactions()
//            R.id.monthly -> showMonthlyTransactions()
//            R.id.yearly -> showYearlyTransactions()
//        }
////        startDate/Button = view!!.findViewById(R.id.selectStartDateButton)
////        endDateButton = view!!.findViewById(R.id.selectEndDateButton)
////        filterButton = view!!.findViewById(R.id.applyDateFilterButton)
//
//        // Set click listeners for the date picking
////        startDateButton.setOnClickListener {
////            showDatePicker(true)
////        }
////
////        endDateButton.setOnClickListener {
////            showDatePicker(false)
////        }
////
////         Set click listener for the filter button
////        filterButton.setOnClickListener {
////             Validate startDate and endDate before filtering
////            if (startDate > 0 && endDate > 0) {
////                filterByDateRange(startDate, endDate)
////            } else {
////                Toast.makeText(
////                    requireContext(),
////                    "Please select both start and end dates",
////                    Toast.LENGTH_SHORT
////                ).show()
////            }
////        }
//        binding.toggleSelector.addOnButtonCheckedListener{ toggleSelector,checkedId,isChecked ->
//            if(isChecked){
//                when(checkedId) {
//                    R.id.all -> showAllTransactions()
//                    R.id.monthly -> showMonthlyTransactions()
//                    R.id.yearly -> showYearlyTransactions()
//                }
//            }
//
//
//
//        }
//
//
//
//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    requireActivity().finish()
//                }
//            }
//            )
//        return binding.root
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    private fun showAllTransactions() {
//        binding.transactionRecyclerView.visibility = View.VISIBLE
//        binding.selectors.visibility = View.GONE
//        binding.monthlyCard.visibility = View.GONE
//        binding.yearSpinner.visibility = View.GONE
//        binding.text1.visibility = View.GONE
//       binding.title.text = "All Transactions"
//        viewModel.getTransaction().observe(viewLifecycleOwner) { transactionList ->
//            if (transactionList.isEmpty()) {
//                binding.noTransactionsDoneText.text = "No transaction done Yet"
//                binding.noTransactionsDoneText.visibility = View.VISIBLE
//                binding.transactionRecyclerView.visibility = View.GONE
//            } else {
//                binding.noTransactionsDoneText.visibility = View.GONE
//                binding.transactionRecyclerView.visibility = View.VISIBLE
//                binding.transactionRecyclerView.layoutManager =
//                    LinearLayoutManager(requireContext())
//                binding.transactionRecyclerView.adapter =
//                    TransactionAdapter(
//                        requireContext(),
//                        requireActivity(), "AllTransactions", transactionList
//                    )
//            }
//        }
//    }
//
//    private fun setToStartOfDay(date: Long): Long {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = date
//        calendar.set(Calendar.HOUR_OF_DAY, 0)
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.set(Calendar.SECOND, 0)
//        return calendar.timeInMillis
//    }
//
//    private fun setToEndOfDay(date: Long): Long {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = date
//        calendar.set(Calendar.HOUR_OF_DAY, 23)
//        calendar.set(Calendar.MINUTE, 59)
//        calendar.set(Calendar.SECOND, 59)
//        return calendar.timeInMillis
//    }
//
//
//    @SuppressLint("SimpleDateFormat", "SetTextI18n")
//    private fun filterByDateRange(startDate: Long, endDate: Long) {
//        // Adjust the time part of dates to be inclusive
//        val adjustedStartDate = setToStartOfDay(startDate)
//        val adjustedEndDate = setToEndOfDay(endDate)
//
//        viewModel.getTransactionsByDateRange(adjustedStartDate, adjustedEndDate)
//            .observe(viewLifecycleOwner) { transactionList ->
//                if (transactionList.isEmpty()) {
//                    binding.noTransactionsDoneText.text = "No transactions in the selected date range"
//                    binding.noTransactionsDoneText.visibility = View.VISIBLE
//                    binding.monthlyCard.visibility = View.GONE
//                    binding.transactionRecyclerView.visibility = View.GONE
//                    binding.text1.visibility = View.GONE
//                } else {
//                    binding.monthlyCard.visibility = View.VISIBLE
//                    binding.noTransactionsDoneText.visibility = View.GONE
//                    binding.transactionRecyclerView.visibility = View.VISIBLE
//                    binding.text1.visibility = View.VISIBLE
//                    binding.transactionRecyclerView.layoutManager =
//                        LinearLayoutManager(requireContext())
//                    binding.transactionRecyclerView.adapter = TransactionAdapter(
//                        requireContext(),
//                        requireActivity(),
//                        "AllTransactions",
//                        transactionList.reversed()
//                    )
//
//                var totalExpense = 0.0
//                var totalGoal = userDetails.getString("MonthlyBudget", "0")?.toFloat()!!
//                var totalFood = 0.0f
//                var totalShopping = 0.0f
//                var totalTransport = 0.0f
//                var totalHealth = 0.0f
//                var totalOthers = 0.0f
//                var totalAcademics = 0.0f
//
//                for (i in transactionList) {
//                    totalExpense += i.amount
//                    when (i.category) {
//                        "Food" -> {
//                            totalFood += (i.amount.toFloat())
//                        }
//
//                        "Shopping" -> {
//                            totalShopping += (i.amount.toFloat())
//                        }
//
//                        "Transport" -> {
//                            totalTransport += (i.amount.toFloat())
//                        }
//
//                        "Health" -> {
//                            totalHealth += (i.amount.toFloat())
//                        }
//
//                        "Other" -> {
//                            totalOthers += (i.amount.toFloat())
//                        }
//
//                        "Education" -> {
//                            totalAcademics += (i.amount.toFloat())
//                        }
//                    }
//                }
//
//                binding.expense.text = "$${totalExpense.toInt()}"
//                binding.budget.text = "$${totalGoal.toInt()}"
//                binding.date.text = "Selected Date Range"
//                if (totalExpense > totalGoal) {
//                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
//                    binding.expense.setTextColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                } else {
//                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
//                }
//
//                showPiChart(totalFood, totalShopping, totalHealth, totalOthers, totalTransport, totalAcademics)
//                    Log.d("FilteredTransactions", transactionList.toString())
//            }
//        }
//    }
//
//    private fun showPiChart(totalFood: Float, totalShopping: Float, totalHealth: Float, totalOthers: Float, totalTransport: Float, totalAcademics: Float) {
//        mPieChart = binding.piechart
//        mPieChart.clearChart()
//
//        mPieChart.addPieSlice(
//            PieModel("Food", totalFood, ContextCompat.getColor(requireContext(), R.color.yellow))
//        )
//        mPieChart.addPieSlice(
//            PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue))
//        )
//        mPieChart.addPieSlice(
//            PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.red))
//        )
//        mPieChart.addPieSlice(
//            PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.brown))
//        )
//        mPieChart.addPieSlice(
//            PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.violet))
//        )
//        mPieChart.addPieSlice(
//            PieModel("Academics", totalAcademics, ContextCompat.getColor(requireContext(), R.color.green))
//        )
//
//        val totalGoal = userDetails.getString("MonthlyBudget", "0")?.toFloat()!!
//
//        if (totalGoal > totalFood + totalShopping + totalHealth + totalOthers + totalTransport + totalAcademics) {
//            mPieChart.addPieSlice(
//                PieModel(
//                    "Empty",
//                    totalGoal - (totalFood + totalShopping + totalHealth + totalOthers + totalTransport + totalAcademics),
//                    ContextCompat.getColor(requireContext(), R.color.background_deep)
//                )
//            )
//        }
//
//        mPieChart.startAnimation()
//    }
//
//    @SuppressLint("SimpleDateFormat", "SetTextI18n")
//    private fun showMonthlyTransactions() {
//        binding.text.text = "Monthly Budget"
//        year=SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()).toInt()
//        val list = mutableListOf(2020)
//        list.clear()
//        for(i in year downTo 2020){
//            list += i
//        }
//        val yearAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,list)
//        binding.yearSpinner.setAdapter(yearAdapter)
//        setMonth(binding.January,binding.January)
//        showMonthsTransaction()
//        binding.transactionRecyclerView.visibility = View.VISIBLE
//        binding.selectors.visibility = View.VISIBLE
//        binding.monthlyCard.visibility = View.VISIBLE
//        binding.yearSpinner.visibility = View.VISIBLE
//        binding.text1.visibility = View.VISIBLE
//        binding.title.text = "Monthly Transactions"
//        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                year=binding.yearSpinner.selectedItem.toString().toInt()
//                showMonthsTransaction()
//            } // to close the onItemSelected
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                year=binding.yearSpinner.selectedItem.toString().toInt()
//                showMonthsTransaction()
//            }
//        }
//
//    }
//    private fun showDatePicker(isStartDate: Boolean) {
//        val currentDate = Calendar.getInstance()
//        val year = currentDate.get(Calendar.YEAR)
//        val month = currentDate.get(Calendar.MONTH)
//        val day = currentDate.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(
//            requireContext(),
//            { _, selectedYear, selectedMonth, selectedDay ->
//                val selectedCalendar = Calendar.getInstance()
//                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val formattedDate = dateFormat.format(selectedCalendar.time)
//
//                // Set the chosen date to the corresponding button
//                if (isStartDate) {
//                    startDateButton.text = formattedDate
//                    startDate = selectedCalendar.timeInMillis
//                } else {
//                    endDateButton.text = formattedDate
//                    endDate = selectedCalendar.timeInMillis
//                }
//            },
//            year,
//            month,
//            day
//        )
//
//        // Optionally, set a minimum date to prevent choosing future dates
//        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
//
//        datePickerDialog.show()
//    }
//    @SuppressLint("SetTextI18n")
//    private fun showMonthsTransaction(){
//        mPieChart=binding.piechart
//        mPieChart.clearChart()
//        totalExpense = 0.0
//        totalGoal = userDetails.getString("MonthlyBudget","0")?.toFloat()!!
//        totalFood = 0.0f
//        totalShopping = 0.0f
//        totalTransport=0.0f
//        totalHealth = 0.0f
//        totalOthers = 0.0f
//        totalAcademics = 0.0f
//        viewModel.getMonthlyTransaction(monthInt,year).observe(viewLifecycleOwner
//        ) { transactionList ->
//            if (transactionList.isEmpty()) {
//                binding.noTransactionsDoneText.text = "No transaction done on $month $year "
//                binding.noTransactionsDoneText.visibility = View.VISIBLE
//                binding.monthlyCard.visibility = View.GONE
//                binding.transactionRecyclerView.visibility = View.GONE
//                binding.text1.visibility = View.GONE
//            } else {
//                binding.monthlyCard.visibility = View.VISIBLE
//                binding.noTransactionsDoneText.visibility = View.GONE
//                binding.transactionRecyclerView.visibility = View.VISIBLE
//                binding.text1.visibility = View.VISIBLE
//                binding.transactionRecyclerView.layoutManager =
//                    LinearLayoutManager(requireContext())
//                binding.transactionRecyclerView.adapter = TransactionAdapter(
//                    requireContext(),
//                    requireActivity(),
//                    "AllTransactions",
//                    transactionList.reversed()
//                )
//
//                for (i in transactionList) {
//                    totalExpense += i.amount
//                    when (i.category) {
//                        "Food" -> {
//                            totalFood += (i.amount.toFloat())
//                        }
//
//                        "Shopping" -> {
//                            totalShopping += (i.amount.toFloat())
//                        }
//
//                        "Transport" -> {
//                            totalTransport += (i.amount.toFloat())
//                        }
//
//                        "Health" -> {
//                            totalHealth += (i.amount.toFloat())
//                        }
//
//                        "Other" -> {
//                            totalOthers += (i.amount.toFloat())
//                        }
//
//                        "Education" -> {
//                            totalAcademics += (i.amount.toFloat())
//                        }
//                    }
//                }
//                binding.expense.text = "$${totalExpense.toInt()}"
//                binding.budget.text = "$${totalGoal.toInt()}"
//                binding.date.text = "${month} ${year}"
//                if (totalExpense > totalGoal) {
//                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
//                    binding.expense.setTextColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                } else {
//                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
//                }
//                showPiChart()
//            }
//
//        }
//    }
//    private fun showPiChart() {
//        mPieChart.addPieSlice(PieModel("Food", totalFood, ContextCompat.getColor(requireContext(), R.color.yellow)))
//        mPieChart.addPieSlice(PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue)))
//        mPieChart.addPieSlice(PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.red)))
//        mPieChart.addPieSlice(PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.brown)))
//        mPieChart.addPieSlice(PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.violet)))
//        mPieChart.addPieSlice(PieModel("Academics", totalAcademics, ContextCompat.getColor(requireContext(), R.color.green)))
//
//        if (totalGoal>totalExpense){
//            mPieChart.addPieSlice(PieModel("Empty",totalGoal-(totalExpense.toFloat()) , ContextCompat.getColor(requireContext(), R.color.background_deep)))
//        }
//
//        mPieChart.startAnimation()
//    }
//
//
//    @SuppressLint("SetTextI18n", "SimpleDateFormat")
//    private fun showYearlyTransactions() {
//        binding.title.text = "Yearly Transactions"
//        binding.text.text = "Yearly Budget"
//        year=SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()).toInt()
//        val list = mutableListOf(2020)
//        list.clear()
//        for(i in year downTo 2020){
//            list += i
//        }
//        val yearAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,list)
//        binding.yearSpinner.setAdapter(yearAdapter)
//        binding.transactionRecyclerView.visibility = View.VISIBLE
//        binding.selectors.visibility = View.GONE
//        binding.monthlyCard.visibility = View.VISIBLE
//        binding.yearSpinner.visibility = View.VISIBLE
//        binding.text1.visibility = View.VISIBLE
//        showYearlyTransaction()
//        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                year=binding.yearSpinner.selectedItem.toString().toInt()
//                showYearlyTransaction()
//            } // to close the onItemSelected
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                year=binding.yearSpinner.selectedItem.toString().toInt()
//                showYearlyTransaction()
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun showYearlyTransaction(){
//        mPieChart=binding.piechart
//        mPieChart.clearChart()
//        totalExpense = 0.0
//        totalGoal = userDetails.getString("YearlyBudget","0")?.toFloat()!!
//        totalFood = 0.0f
//        totalShopping = 0.0f
//        totalTransport=0.0f
//        totalHealth = 0.0f
//        totalOthers = 0.0f
//        totalAcademics = 0.0f
//        viewModel.getYearlyTransaction(year).observe(viewLifecycleOwner) { transactionList ->
//            if (transactionList.isEmpty()) {
//                binding.noTransactionsDoneText.text = "No transaction done on Year $year "
//                binding.noTransactionsDoneText.visibility = View.VISIBLE
//                binding.monthlyCard.visibility = View.GONE
//                binding.transactionRecyclerView.visibility = View.GONE
//                binding.text1.visibility = View.GONE
//            } else {
//                binding.monthlyCard.visibility = View.VISIBLE
//                binding.noTransactionsDoneText.visibility = View.GONE
//                binding.transactionRecyclerView.visibility = View.VISIBLE
//                binding.text1.visibility = View.VISIBLE
//                binding.transactionRecyclerView.layoutManager =
//                    LinearLayoutManager(requireContext())
//                binding.transactionRecyclerView.adapter = TransactionAdapter(
//                    requireContext(),
//                    requireActivity(),
//                    "AllTransactions",
//                    transactionList.reversed()
//                )
//
//                for (i in transactionList) {
//                    totalExpense += i.amount
//                    when (i.category) {
//                        "Food" -> {
//                            totalFood += (i.amount.toFloat())
//                        }
//
//                        "Shopping" -> {
//                            totalShopping += (i.amount.toFloat())
//                        }
//
//                        "Transport" -> {
//                            totalTransport += (i.amount.toFloat())
//                        }
//
//                        "Health" -> {
//                            totalHealth += (i.amount.toFloat())
//                        }
//
//                        "Other" -> {
//                            totalOthers += (i.amount.toFloat())
//                        }
//
//                        "Education" -> {
//                            totalAcademics += (i.amount.toFloat())
//                        }
//                    }
//                }
//                binding.expense.text = "$${totalExpense.toInt()}"
//                binding.budget.text = "$${totalGoal.toInt()}"
//                binding.date.text = "Year: ${year}"
//                if (totalExpense > totalGoal) {
//                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
//                    binding.expense.setTextColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                } else {
//                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
//                }
//                showPiChart()
//            }
//        }
//    }
//
//
//    private fun setListener() {
//        binding.January.setOnClickListener(this)
//        binding.February.setOnClickListener(this)
//        binding.March.setOnClickListener(this)
//        binding.April.setOnClickListener(this)
//        binding.May.setOnClickListener(this)
//        binding.June.setOnClickListener(this)
//        binding.July.setOnClickListener(this)
//        binding.August.setOnClickListener(this)
//        binding.September.setOnClickListener(this)
//        binding.October.setOnClickListener(this)
//        binding.November.setOnClickListener(this)
//        binding.December.setOnClickListener(this)
//
//    }
//
//    override fun onClick(v: View?) {
//        when (v) {
//            binding.January -> {
//                setMonth(v,binding.January)
//                monthInt=1
//                showMonthsTransaction()
//            }
//            binding.February -> {
//                setMonth(v,binding.February)
//                monthInt=2
//                showMonthsTransaction()
//            }
//            binding.March -> {
//                setMonth(v,binding.March)
//                monthInt=3
//                showMonthsTransaction()
//            }
//            binding.April -> {
//                setMonth(v,binding.April)
//                monthInt=4
//                showMonthsTransaction()
//            }
//            binding.May -> {
//                setMonth(v,binding.May)
//                monthInt=5
//                showMonthsTransaction()
//            }
//            binding.June -> {
//                setMonth(v,binding.June)
//                monthInt=6
//                showMonthsTransaction()
//            }
//            binding.July -> {
//                setMonth(v,binding.July)
//                monthInt=7
//                showMonthsTransaction()
//            }
//            binding.August -> {
//                setMonth(v,binding.August)
//                monthInt=8
//                showMonthsTransaction()
//            }
//            binding.September -> {
//                setMonth(v,binding.September)
//                monthInt=9
//                showMonthsTransaction()
//            }
//            binding.October -> {
//                setMonth(v,binding.October)
//                monthInt=10
//                showMonthsTransaction()
//            }
//            binding.November -> {
//                setMonth(v,binding.November)
//                monthInt=11
//                showMonthsTransaction()
//            }
//            binding.December -> {
//                setMonth(v,binding.December)
//                monthInt=12
//                showMonthsTransaction()
//            }
//        }
//    }
//
//    private fun setMonth(v: View, button: MaterialButton) {
//        month = button.text.toString()
//        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_100))
//        button.setStrokeColorResource(R.color.blue_100)
//        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//
//        when (v) {
//            binding.January -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.February -> {
//                removeBackground(binding.January)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.March -> {
//                removeBackground(binding.February)
//                removeBackground(binding.January)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.April -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.January)
//                removeBackground(binding.May)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.May -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.January)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.June -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.January)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.July -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.January)
//                removeBackground(binding.June)
//                removeBackground(binding.January)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.August -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.January)
//                removeBackground(binding.July)
//                removeBackground(binding.January)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.September -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.January)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.January)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.October -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.January)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.January)
//                removeBackground(binding.November)
//                removeBackground(binding.December)
//            }
//            binding.November -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.January)
//                removeBackground(binding.June)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.January)
//                removeBackground(binding.December)
//            }
//            binding.December -> {
//                removeBackground(binding.February)
//                removeBackground(binding.March)
//                removeBackground(binding.April)
//                removeBackground(binding.May)
//                removeBackground(binding.January)
//                removeBackground(binding.July)
//                removeBackground(binding.August)
//                removeBackground(binding.September)
//                removeBackground(binding.October)
//                removeBackground(binding.November)
//                removeBackground(binding.January)
//            }
//        }
//    }
//
//    private fun removeBackground(button: MaterialButton) {
//        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
//        button.setIconTintResource(R.color.textSecondary)
//        button.setStrokeColorResource(R.color.textSecondary)
//        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary))
//    }
//
//
//}