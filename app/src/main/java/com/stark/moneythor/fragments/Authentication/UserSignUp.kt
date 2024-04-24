package com.stark.moneythor.fragments.Authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
//import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.stark.moneythor.R
//import com.stark.moneythor.composegooglesignincleanarchitecture.presentation.profile.ProfileScreen
//import com.stark.moneythor.composegooglesignincleanarchitecture.presentation.sign_in.SignInScreen
import com.stark.moneythor.databinding.FragmentUserSignUpBinding
import kotlinx.coroutines.launch
import java.util.*


class UserSignUp: Fragment() {

    lateinit var binding: FragmentUserSignUpBinding
    lateinit var client: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserSignUpBinding.inflate(inflater, container, false)









        setUpSignUp()
        binding.googleSignUp.setOnClickListener {
            signIn()
        }

        return binding.root
    }

    private fun setUpSignUp() {
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null) {
            goToNextPage()
        }
        googleCall()
    }

    private fun googleCall() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        client = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signIn() {
        val signInIntent: Intent = client.signInIntent
        Log.d("SignIn", "Launching sign-in intent")
        getResult.launch(signInIntent)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                goToNextPage()
            } else {
                notifyUser("Check Your Network Connection and Try again")
            }
        }

    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun goToNextPage() {
        findNavController(requireActivity(), R.id.fragmentContainerView2)
            .navigate(
                R.id.goToUserDetails,
                null,
                NavOptions.Builder()
                    .setPopUpTo(
                        R.id.userSignUp,
                        true
                    ).build()
            )
    }
}
/*
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
        } catch (e: ApiException) {
        }
    }

    private fun handle(){
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireActivity())
        val credential =
            GoogleAccountCredential.usingOAuth2(requireContext(), Collections.singleton(Scopes.DRIVE_FILE))
        credential.selectedAccount = googleSignInAccount!!.account
        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(getString(com.stark.moneythor.R.string.app_name))
            .build()
        Thread {
            Download(googleDriveService)
        }.start()


    }

    private fun handleSignInIntent(data: Intent?) {

        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener {
                val credential: GoogleAccountCredential = GoogleAccountCredential
                    .usingOAuth2(requireActivity(), Collections.singleton(DriveScopes.DRIVE_FILE))
                credential.setSelectedAccount(it.account)
                val googleDriveDevices: Drive = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential)
                    .setApplicationName("Buckoid")
                    .build()

                Thread(Runnable {
                     Download(googleDriveDevices)
                }).start()
            }
            .addOnFailureListener{

            }

    }

    private fun upload(googleDriveDevices:Drive){
        var storageFile:com.google.api.services.drive.model.File? = null
        storageFile?.setParents(Collections.singletonList("appDataFolder"))
        storageFile?.setName("Buckoid_Backup_Transaction")

        val filePath:java.io.File = java.io.File(dbPath)
        val mediaContent:FileContent = FileContent("",filePath)
        try {
            val file: com.google.api.services.drive.model.File? = googleDriveDevices.files().create(storageFile,mediaContent).execute();
            if (file != null) {
                Log.w("@@@","Filename: %s File ID: %s ${file.getName()}, ${file.getId()}")
            }
        }
        catch(e: UserRecoverableAuthIOException){
            Log.w("@@@","errorAuthIO:"+e.message.toString())
        }
        catch (e:Exception) {
            Log.w("@@@","error:"+e.message.toString())
        }
    }

    private fun Download(googleDriveService:Drive) {
        try {
            val dir = File("/data/data/com.stark.moneythor/databases")
            if (dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    File(dir, children[i]).delete()
                }
            }
            val files: FileList = googleDriveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(10)
                .execute()
            if (files.files.size == 0) Log.e("@@@", "No DB file exists in Drive")
            for (file in files.files) {
                Log.e("@@@", "Found file: ${file.name}, ${file.id}, ${file.createdTime}")
                if (file.name.equals("Buckoid_Backup_Transaction")) {
                    val outputStream: OutputStream = FileOutputStream(dbPath)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                }
            }
        } catch (e: IOException) {
            Log.w("@@@","error:"+e.message.toString())
        }
    }*/

//}