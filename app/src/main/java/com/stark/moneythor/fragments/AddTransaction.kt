package com.stark.moneythor.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.stark.moneythor.Model.Transaction
import com.stark.moneythor.R
import com.stark.moneythor.ViewModel.TransactionViewModel
import com.stark.moneythor.databinding.FragmentAddTransactionBinding

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*


class AddTransaction : Fragment(), View.OnClickListener {
   val transactions by navArgs<AddTransactionArgs>()
   private lateinit var binding: FragmentAddTransactionBinding
   lateinit var userDetails: SharedPreferences
   private var category = ""
    var day=0
    var month=0
    var year=0

   private val viewModel: TransactionViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        getActivity()?.getWindow()?.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.background))
        binding =  FragmentAddTransactionBinding.inflate(inflater, container, false)
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation)
        bottomNav.visibility = View.GONE
        setListener(binding)
        datePicker(binding)
        userDetails = requireActivity().getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
        if(transactions.from){
            setDatas()
            binding.addTransaction.setText("Save Transaction")
            binding.titleAddTransacttion.setText("Edit Transaction")
            binding.back.setOnClickListener {
                val arg = AddTransactionDirections.actionAddTransactionToTransactionDetails(transactions.data,"AddTransaction")
                Navigation.findNavController(binding.root)
                    .navigate(arg)
            }
        }else{
            binding.back.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.action_addTransaction_to_dashboard2) }
        }
        transactions.data?.let { transaction ->
            binding.editTitle.setText(transaction.title)
            binding.editDate.setText(transaction.date)
            binding.editMoney.setText(transaction.amount.toString())
            binding.editNote.setText(transaction.note)
            when (transaction.type) {
                "Income" -> {
                    binding.incomeRadioButton.isChecked = true
                    binding.expenseRadioButton.isChecked = false
                }
                "Expense" -> {
                    binding.expenseRadioButton.isChecked = true
                    binding.incomeRadioButton.isChecked = false
                }
            }

            // Set category (you may want to modify this part based on your UI logic)
            setCategoryForRepeatTransaction(transaction.category)
        }
        if (!transactions.from) {
            // If it's a new transaction, set the default date to today
            val currentDate = Calendar.getInstance()
            day = currentDate.get(Calendar.DAY_OF_MONTH)
            month = currentDate.get(Calendar.MONTH) + 1 // Months are zero-based
            year = currentDate.get(Calendar.YEAR)

            val defaultDate = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(currentDate.time)
            binding.editDate.setText(defaultDate)
        } else {
            // If it's an existing transaction, set the date from the transaction data
            binding.editDate.setText(transactions.data.date)
            day = transactions.data.day
            month = transactions.data.month
            year = transactions.data.year
        }
        binding.addTransaction.setOnClickListener{ addNewTransaction() }
        return binding.root
    }
    private fun setCategoryForRepeatTransaction(category: String) {
        // Clear background for all buttons
        removeBackground(binding.food)
        removeBackground(binding.shopping)
        removeBackground(binding.transport)
        removeBackground(binding.health)
        removeBackground(binding.others)
        removeBackground(binding.academics)

        // Set background for the selected category button
        when (category) {
            "Food" -> setCategory(binding.food, binding.food)
            "Shopping" -> setCategory(binding.shopping, binding.shopping)
            "Transport" -> setCategory(binding.transport, binding.transport)
            "Health" -> setCategory(binding.health, binding.health)
            "Other" -> setCategory(binding.others, binding.others)
            "Education" -> setCategory(binding.academics, binding.academics)
        }
    }
    private fun setListener(binding: FragmentAddTransactionBinding) {
        binding.food.setOnClickListener(this)
        binding.shopping.setOnClickListener(this)
        binding.transport.setOnClickListener(this)
        binding.health.setOnClickListener(this)
        binding.others.setOnClickListener(this)
        binding.academics.setOnClickListener(this)

    }


    private fun setDatas(){
        binding.editTitle.setText(transactions.data.title)
        binding.editDate.setText(transactions.data.date)
        binding.editMoney.setText(transactions.data.amount.toString())
        binding.editNote.setText(transactions.data.note)
        when (transactions.data.type) {
            "Income" -> {
                binding.incomeRadioButton.isChecked = true
                binding.expenseRadioButton.isChecked = false
            }
            "Expense" -> {
                binding.expenseRadioButton.isChecked = true
                binding.incomeRadioButton.isChecked = false
            }
        }
        category=transactions.data.category
        when (category) {
            "Food" -> {
                setCategory(binding.food, binding.food)
            }
            "Shopping" -> {
                setCategory(binding.shopping, binding.shopping)
            }
            "Transport" -> {
                setCategory(binding.transport, binding.transport)
            }
            "Health" -> {
                setCategory(binding.health, binding.health)
            }
            "Other" -> {
                setCategory(binding.others, binding.others)
            }
            "Education" -> {
                setCategory(binding.academics, binding.academics)
            }
        }
    }
    private fun addNewTransaction() {
        val title = binding.editTitle.text.toString()
        val amount = binding.editMoney.text.toString()
        val note = binding.editNote.text.toString()
        val date = binding.editDate.text.toString()
        var trantype = ""
        if (binding.expenseRadioButton.isChecked) { trantype = "Expense" }
        else if (binding.incomeRadioButton.isChecked){ trantype = "Income" }
        val transactionType = trantype

        if (title.isBlank() || amount.isBlank() || note.isBlank() || date.isBlank() || category.isBlank() || transactionType.isBlank()) {
            Toast.makeText(context, "Enter all required details", Toast.LENGTH_SHORT).show()
        } else {
            val newTransaction = Transaction(
                id = transactions.data?.id,
                type = transactionType,
                title = title,
                amount = amount.replace(",",".").toDouble(),
                note = note,
                date = date,
                day = day,
                month = month,
                year = year,
                category = category
            )


            if (transactions.from) {
                // Update existing transaction
                viewModel.updateTransaction(newTransaction)
                Log.d("ViewModel", "Updating transaction: $newTransaction")
                Toast.makeText(context, "Transaction Updated Successfully", Toast.LENGTH_SHORT).show()
                val action = AddTransactionDirections.actionAddTransactionToTransactionDetails(newTransaction, "AddTransaction")
                Navigation.findNavController(binding.root).navigate(action)
            } else {
                // Add new transaction
                viewModel.addTransaction(newTransaction)
                Toast.makeText(context, "Transaction Added Successfully", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(binding.root).navigate(R.id.action_addTransaction_to_dashboard2)
            }
        }
    }



    @SuppressLint("SimpleDateFormat")
    fun datePicker(binding:FragmentAddTransactionBinding){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH) + 1
        year = cal.get(Calendar.YEAR)

        binding.editDate.setText(SimpleDateFormat("dd MMMM yyyy").format(cal.time))

        val dateSetListener = OnDateSetListener { _, selectedYear, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, selectedYear)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            var myFormat = "dd MMMM yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.editDate.setText(sdf.format(cal.time))

            myFormat = "dd"
            val sdfDay = SimpleDateFormat(myFormat, Locale.US)
            day = sdfDay.format(cal.time).toInt()

            myFormat = "MM"
            val sdfMonth = SimpleDateFormat(myFormat, Locale.US)
            month = sdfMonth.format(cal.time).toInt()

            myFormat = "yyyy"
            val sdfYear = SimpleDateFormat(myFormat, Locale.US)
            year = sdfYear.format(cal.time).toInt()
        }

        binding.editDate.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


//        val cal = Calendar.getInstance()
//        binding.editDate.setText(SimpleDateFormat("dd MMMM  yyyy").format(System.currentTimeMillis()))
//        day = SimpleDateFormat("dd").format(System.currentTimeMillis()).toInt()
//        month = SimpleDateFormat("MM").format(System.currentTimeMillis()).toInt()
//        year = SimpleDateFormat("yyyy").format(System.currentTimeMillis()).toInt()
//        val dateSetListener = OnDateSetListener { _, Year, monthOfYear, dayOfMonth ->
//            cal.set(Calendar.YEAR, Year)
//            cal.set(Calendar.MONTH, monthOfYear)
//            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            var myFormat = "dd MMMM  yyyy" // mention the format you need
//            var sdf = SimpleDateFormat(myFormat, Locale.US)
//            binding.editDate.setText(sdf.format(cal.time))
//            myFormat="dd"
//            sdf = SimpleDateFormat(myFormat, Locale.US)
//            day =sdf.format(cal.time).toInt()
//            myFormat="MM"
//            sdf = SimpleDateFormat(myFormat, Locale.US)
//            month = sdf.format(cal.time).toInt()
//            myFormat="yyyy"
//            sdf = SimpleDateFormat(myFormat, Locale.US)
//            year = sdf.format(cal.time).toInt()
//
//        }
//
//        binding.editDate.setOnClickListener {
//            DatePickerDialog(requireContext(), dateSetListener,
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)).show()
//        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.food -> {
                setCategory(v,binding.food)
            }
            binding.shopping -> {
                setCategory(v,binding.shopping)
            }
            binding.transport -> {
                setCategory(v,binding.transport)
            }
            binding.health -> {
                setCategory(v,binding.health)
            }
            binding.others -> {
                setCategory(v,binding.others)
            }
            binding.academics -> {
                setCategory(v,binding.academics)
            }
        }
    }

    private fun setCategory(v: View, button: MaterialButton) {
        category = button.text.toString()
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mtrl_btn_text_btn_bg_color_selector))
        button.setIconTintResource(R.color.blue_100)
        button.setStrokeColorResource(R.color.blue_100)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_100))

        when (v) {
            binding.food -> {
                removeBackground(binding.shopping)
                removeBackground(binding.transport)
                removeBackground(binding.health)
                removeBackground(binding.others)
                removeBackground(binding.academics)
            }
            binding.shopping -> {
                removeBackground(binding.food)
                removeBackground(binding.transport)
                removeBackground(binding.health)
                removeBackground(binding.others)
                removeBackground(binding.academics)
            }
            binding.transport -> {
                removeBackground(binding.shopping)
                removeBackground(binding.food)
                removeBackground(binding.health)
                removeBackground(binding.others)
                removeBackground(binding.academics)
            }
            binding.health -> {
                removeBackground(binding.shopping)
                removeBackground(binding.transport)
                removeBackground(binding.food)
                removeBackground(binding.others)
                removeBackground(binding.academics)
            }
            binding.others -> {
                removeBackground(binding.shopping)
                removeBackground(binding.transport)
                removeBackground(binding.health)
                removeBackground(binding.food)
                removeBackground(binding.academics)
            }
            binding.academics -> {
                removeBackground(binding.shopping)
                removeBackground(binding.transport)
                removeBackground(binding.health)
                removeBackground(binding.others)
                removeBackground(binding.food)
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

