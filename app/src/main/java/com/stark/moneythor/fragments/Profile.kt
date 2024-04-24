package com.stark.moneythor.fragments

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.TaskStackBuilder
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.api.services.drive.DriveScopes
import com.google.firebase.auth.FirebaseAuth
import com.stark.moneythor.MainActivity
import com.stark.moneythor.Model.Profile
import com.stark.moneythor.R
import com.stark.moneythor.databinding.FragmentProfileBinding
import com.stark.moneythor.fragments.Authentication.UserSignUp


class Profile : Fragment() {
    lateinit var client: GoogleSignInClient

    lateinit var binding:FragmentProfileBinding
    lateinit var userDetails: SharedPreferences
    var isNight:Boolean = false
    lateinit var profileModel: Profile
    private lateinit var auth: FirebaseAuth

    //finger print
    var isFingerPrintEnabled:Boolean = false
    var fingerprintChecked:Boolean = false
    private var cancellationSignal:CancellationSignal?=null
    private val authenticationCallback:BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback(){
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                    fingerPrintLockEnable()
                }


                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    if (fingerprintChecked) {
                        isFingerPrintEnabled = true
                        val editor: SharedPreferences.Editor = userDetails.edit()
                        editor.putBoolean("fingerprint_enabled", true)
                        editor.apply()
                    } else {
                        isFingerPrintEnabled = false
                        val editor: SharedPreferences.Editor = userDetails.edit()
                        editor.putBoolean("fingerprint_enabled", false)
                        editor.apply()
                    }
                }
            }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        getActivity()?.getWindow()?.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.cardBackground))
        binding =  FragmentProfileBinding.inflate(inflater, container, false)
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE
        setData()
        fingerPrintLockEnable()

        auth = FirebaseAuth.getInstance()

        binding.signOutButton.setOnClickListener {
            signOut()

        }

        return binding.root

    }

    private fun signOut() {
//        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        try {
            // Use the generated action ID for navigating to the user signup fragment
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_user_signup)
        } catch (e: IllegalArgumentException) {
            // Handle the case where the action is not found
            e.printStackTrace()
        }
//        Navigation.findNavController(binding.root).navigate(R.id.userSignUp)

//        requireActivity().finish()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
//            .build()
//        client = GoogleSignIn.getClient(requireActivity(), gso)
//        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView2)
//            .navigate(R.id.goToUserDetails,null,NavOptions.Builder().setPopUpTo(R.id.userSignUp,true).build())








//        Navigation.findNavController(binding.root).navigate(R.id.userSignUp)
    }


    @SuppressLint("SetTextI18n")
    private fun setData() {
        userDetails = requireActivity().getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
        nightMode()
        profileModel = Profile(requireContext())
        val name=profileModel.name
        binding.profileName.text = name
        binding.mailId.text = profileModel.email
        Glide.with(requireActivity()).load(profileModel.profilePic).into(binding.profilePic)

        val monthlyBudget = userDetails.getString("MonthlyBudget","0")
        val yearlyBudget = userDetails.getString("YearlyBudget","0")

        binding.monthlyBudget.text = "$$monthlyBudget"
        binding.yearlyBudget.text = "$$yearlyBudget"

        binding.edit.setOnClickListener {
            openEditDialog(monthlyBudget,yearlyBudget)
        }
        binding.share.setOnClickListener{
            try {
                Toast.makeText(
                    requireActivity(),
                    "Working on this wait for update",
                    Toast.LENGTH_SHORT
                ).show()
//                val intent = Intent(Intent.ACTION_SEND)
//                intent.type = "text/plain"
//                intent.putExtra(Intent.EXTRA_SUBJECT, "@string/app_name")
//                intent.putExtra(
//                    Intent.EXTRA_TEXT,
//                    "https://play.google.com"
//                )
//                startActivity(Intent.createChooser(intent, "Share With"))
            } catch (e: Exception) {
                Toast.makeText(
                    requireActivity(),
                    "Working on this wait for update",
                    Toast.LENGTH_SHORT
                ).show()
//                Toast.makeText(
//                    requireActivity(),
//                    "Unable to share at this moment.." + e.message.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }

        binding.rateUs.setOnClickListener{
            try {
                Toast.makeText(
                    requireActivity(),
                    "Working on this wait for update",
                    Toast.LENGTH_SHORT
                ).show()
//                startActivity(
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com")
//                    )
//                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireActivity(),
                    "Working on this wait for update",
                    Toast.LENGTH_SHORT
                ).show()
//                startActivity(
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com")
//                    )
//                )
            }
        }

        binding.aboutUs.setOnClickListener {
            startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.linkedin.com/in/ahmad-aghamammadov/")
                    )
                )

//                Toast.makeText(
//                requireActivity(),
//                "Working on this wait for update",
//                Toast.LENGTH_SHORT
//            ).show()

        }

        binding.backup.setOnClickListener {
            Toast.makeText(requireActivity(),"Purchase Premium to use this feature", Toast.LENGTH_SHORT).show()
//            Toast.makeText(requireActivity(),"Coming Soon wait for an Update.", Toast.LENGTH_SHORT).show()
//            Navigation.findNavController(binding.root).navigate(R.id.openBackupDrive)
        }


        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
            )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun fingerPrintLockEnable() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.passwordToggle.visibility = View.VISIBLE
            isFingerPrintEnabled = userDetails.getBoolean("fingerprint_enabled",false)
            if (isFingerPrintEnabled) {
                binding.passwordSwitchCompact.setChecked(true)
            } else {
                binding.passwordSwitchCompact.setChecked(false)
            }
            binding.passwordSwitchCompact.setOnCheckedChangeListener { buttonView, isChecked ->
                fingerprintChecked = isChecked
                fingerPrintSensor()
            }
        }else{
            binding.passwordToggle.visibility = View.GONE
        }


    }

    fun nightMode(){
        // Configure night-mode switch
        isNight = userDetails.getBoolean("nightMode",true)
       if (isNight) {
            binding.nightSwitchCompat.setChecked(true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            binding.nightSwitchCompat.setChecked(false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.nightSwitchCompat.setOnCheckedChangeListener { buttonView, isChecked -> applyNightMode(isChecked) }

    }

    private fun applyNightMode(checked: Boolean) {
        if (checked) {
            isNight = true
            saveSettingsBoolean("nightMode",true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            isNight = false
            saveSettingsBoolean("nightMode",false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    protected fun restartActivityInvalidateBackstack(activity: MainActivity) {
        val intent = Intent()
        intent.setClass(activity, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(activity)
        stackBuilder.addNextIntentWithParentStack(intent)
        stackBuilder.startActivities(Bundle())
    }
    private fun saveSettingsBoolean(mode: String, isNight: Boolean) {
        val editor: SharedPreferences.Editor = userDetails.edit()
        editor.putBoolean(mode, isNight)
        editor.apply()
        restartActivityInvalidateBackstack(requireActivity() as MainActivity)
    }

    private fun openEditDialog(monthlyBudget: String?,yearlyBudget: String?) {
        val bottomDialog: BottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.bottom_dialog)
        bottomDialog.setContentView(R.layout.update_user_details_dialog)

        val update = bottomDialog.findViewById<Button>(R.id.update)
        val cancel = bottomDialog.findViewById<Button>(R.id.cancel)
        val moneyEditor = bottomDialog.findViewById<TextInputEditText>(R.id.edit_money)
        val year_money_Editor = bottomDialog.findViewById<TextInputEditText>(R.id.edit_year_money)

        moneyEditor?.setText(monthlyBudget)
        year_money_Editor?.setText(yearlyBudget)

        update?.setOnClickListener {
            val monthly_budget = moneyEditor?.text.toString()
            val yearly_budget = year_money_Editor?.text.toString()
            if(monthly_budget == "" || yearly_budget == "") {
                Toast.makeText(requireActivity(), "Name and Budget Cant be empty...", Toast.LENGTH_SHORT).show()
            }else{
                val editor: SharedPreferences.Editor = userDetails.edit()
                editor.putString("MonthlyBudget", monthly_budget)
                editor.putString("YearlyBudget", yearly_budget)
                editor.apply()
                setData()
                bottomDialog.dismiss()
            }

        }
        cancel?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun fingerPrintSensor() {
        checkBiometricSupport()
        val biometricPrompt = BiometricPrompt.Builder(requireActivity())
            .setDeviceCredentialAllowed(true)
            .setTitle("Authentication Required")
            .setDescription("Please enter your PIN / password to continue")
            .build()

        biometricPrompt.authenticate(getCancellationSignal(),
            getMainExecutor(requireContext()),authenticationCallback)


    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")

        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager : KeyguardManager = activity?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure){
            notifyUser("Finger print not enabled in settings")
            return false
        }

        if (ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }

        return if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) == true){
            true
        }else true

    }

    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }




}


