package com.stark.moneythor.fragments

//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.navigation.Navigation
//import com.google.android.gms.auth.UserRecoverableAuthException
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.common.Scopes
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.api.client.extensions.android.http.AndroidHttp
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
//import com.google.api.client.http.FileContent
//import com.google.api.client.json.gson.GsonFactory
//import com.google.api.services.drive.Drive
//import com.google.api.services.drive.model.FileList
//import com.stark.moneythor.R
//import com.stark.moneythor.databinding.FragmentBackupDriveBinding
//import com.stark.moneythor.Model.Profile
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.OutputStream
//import java.util.Collections
//
//class BackupDrive : Fragment() {
//
//    private lateinit var binding: FragmentBackupDriveBinding
//    private lateinit var profileModel: Profile
//    private val dbPath = "/data/data/com.stark.moneythor/databases/Transaction"
//    private val dbPathWal = "/data/data/com.stark.moneythor/databases/Transaction-wal"
//    private val dbPathShm = "/data/data/com.stark.moneythor/databases/Transaction-shm"
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentBackupDriveBinding.inflate(inflater, container, false)
//        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation)
//        bottomNav.visibility = View.GONE
//
//        setData()
//        return binding.root
//    }
//
//    private fun setData() {
//        binding.back.setOnClickListener {
//            Navigation.findNavController(binding.root).navigate(R.id.action_backupDrive_to_profile)
//        }
//
//        binding.backupBtn.setOnClickListener {
//            backupData()
//        }
//    }
//
//    private fun notifyUser(message: String) {
//        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun backupData() {
//        profileModel = Profile(requireContext())
//        val googleSignInAccount = profileModel.account
//        val credential =
//            GoogleAccountCredential.usingOAuth2(requireContext(), Collections.singleton(Scopes.DRIVE_FILE))
//        credential.selectedAccount = googleSignInAccount!!.account
//        val googleDriveService = Drive.Builder(
//            AndroidHttp.newCompatibleTransport(),
//            GsonFactory(),
//            credential
//        )
//            .setApplicationName(getString(R.string.app_name))
//            .build()
//
//        Thread {
//            try {
//                upload(googleDriveService)
//                notifyUser("Backup successful")
//            } catch (e: UserRecoverableAuthException) {
//                startActivityForResult(e.intent, 1)
//            } catch (e: Exception) {
//                Log.e("@@@", "Backup error: ${e.message}")
//                notifyUser("Backup failed")
//            }
//        }.start()
//    }
//
//    private fun upload(googleDriveService: Drive) {
//        uploadFile(googleDriveService, "Transaction", dbPath)
//        uploadFile(googleDriveService, "Transaction-wal", dbPathWal)
//        uploadFile(googleDriveService, "Transaction-shm", dbPathShm)
//    }
//
//    private fun uploadFile(googleDriveService: Drive, fileName: String, filePath: String) {
//        val storageFile = com.google.api.services.drive.model.File()
//        storageFile.name = fileName
//
//        val fileContent = FileContent("", File(filePath))
//        val file = googleDriveService.files().create(storageFile, fileContent).execute()
//        Log.w("@@@", "Filename: ${file?.name}, File ID: ${file?.id}")
//    }
//
//    private fun download(googleDriveService: Drive) {
//        downloadFile(googleDriveService, "Transaction", dbPath)
//        downloadFile(googleDriveService, "Transaction-wal", dbPathWal)
//        downloadFile(googleDriveService, "Transaction-shm", dbPathShm)
//    }
//
//    private fun downloadFile(googleDriveService: Drive, fileName: String, filePath: String) {
//        val files: FileList = googleDriveService.files()
//            .list()
//            .setFields("nextPageToken, files(id, name, createdTime, size)")
//            .setPageSize(10)
//            .execute()
//
//        for (file in files.files) {
//            if (file.name.equals(fileName)) {
//                val outputStream: OutputStream = FileOutputStream(filePath)
//                googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
//                Log.w("@@@", "Downloaded file: ${file.name}, File ID: ${file.id}")
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1) {
//            if (resultCode == -1) { // User granted permission
//                backupData()
//            } else {
//                notifyUser("Permission denied")
//            }
//        }
//    }
//}





//package com.stark.moneythor.fragments
//
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.common.Scopes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import com.stark.moneythor.R
import com.stark.moneythor.databinding.FragmentBackupDriveBinding
import com.stark.moneythor.Model.Profile
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class BackupDrive : Fragment() {

    lateinit var binding:FragmentBackupDriveBinding
    lateinit var profileModel:Profile
    private val dbPath = "/data/data/com.stark.moneythor/databases/Transaction"
    private val dbPathWal = "/data/data/com.stark.moneythor/databases/Transaction-wal"
    private val dbPathShm = "/data/data/com.stark.moneythor/databases/Transaction-shm"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding =  FragmentBackupDriveBinding.inflate(inflater, container, false)
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation)
        bottomNav.visibility = View.GONE

        setData()
        return binding.root
    }

    private fun setData() {
        binding.back.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_backupDrive_to_profile)
        }

        binding.backupBtn.setOnClickListener{
            backupdata()
//            notifyUser("Coming Soon wait for an Update.")
        }
    }

    private fun notifyUser(message: String) {
        Toast.makeText(requireActivity(),message, Toast.LENGTH_SHORT).show()
    }

    private fun backupdata() {
        profileModel = Profile(requireContext())
        val googleSignInAccount = profileModel.account
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
            upload(googleDriveService)
        }.start()
    }

    private fun upload(googleDriveDevices: Drive) {
        val storageFile = com.google.api.services.drive.model.File()
        storageFile.name = "Transaction"

        val storageFileShm = com.google.api.services.drive.model.File()
        storageFileShm.name = "Transaction-shm"

        val storageFileWal = com.google.api.services.drive.model.File()
        storageFileWal.name = "Transaction-wal"

        Log.d("backup", "test")

        val filePath = java.io.File(dbPath)
        val mediaContent = FileContent("", filePath)
        try {
            val file: com.google.api.services.drive.model.File? = googleDriveDevices.files().create(storageFile, mediaContent).execute()
            if (file != null) {
                Log.w("@@@", "Filename: %s File ID: %s ${file.name}, ${file.id}")
            }

            val fileShm: com.google.api.services.drive.model.File? = googleDriveDevices.files().create(storageFileShm, mediaContent).execute()
            if (fileShm != null) {
                Log.w("@@@", "Filename: %s File ID: %s ${fileShm.name}, ${fileShm.id}")
            }

            val fileWal: com.google.api.services.drive.model.File? = googleDriveDevices.files().create(storageFileWal, mediaContent).execute()
            if (fileWal != null) {
                Log.w("@@@", "Filename: %s File ID: %s ${fileWal.name}, ${fileWal.id}")
            }

        } catch (e: UserRecoverableAuthIOException) {
            Log.w("@@@", "errorAuthIO:" + e.message.toString())
        } catch (e: Exception) {
            Log.w("@@@", "error:" + e.message.toString())
        }
    }


//    private fun upload(googleDriveDevices:Drive){
//        val storageFile:com.google.api.services.drive.model.File =com.google.api.services.drive.model.File()
//        storageFile.setName("Transaction")
//
//        val storageFileShm:com.google.api.services.drive.model.File = com.google.api.services.drive.model.File()
//        storageFile.setName("Transaction-shm")
//
//        val storageFileWal:com.google.api.services.drive.model.File = com.google.api.services.drive.model.File()
//        storageFile.setName("Transaction-wal")
//
//        val filePath:java.io.File = java.io.File(dbPath)
//        val mediaContent:FileContent = FileContent("",filePath)
//        try {
//            val file: com.google.api.services.drive.model.File? = googleDriveDevices.files().create(storageFile,mediaContent).execute();
//            if (file != null) {
//                Log.w("@@@","Filename: %s File ID: %s ${file.getName()}, ${file.getId()}")
//            }
//
//
//        }
//        catch(e: UserRecoverableAuthIOException){
//            Log.w("@@@","errorAuthIO:"+e.message.toString())
//        }
//        catch (e:Exception) {
//            Log.w("@@@","error:"+e.message.toString())
//        }
//    }


    private fun Download(googleDriveService:Drive) {
        try {
            val dir = java.io.File("/data/data/com.stark.moneythor/databases")
            if (dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    Log.e("@@@", "Found file: ${children[i]}")
                    java.io.File(dir, children[i]).delete()
                }

                val files: FileList = googleDriveService.files().list()

                .setFields("nextPageToken, files(id, name, createdTime, size)")
                .setPageSize(10)
                .execute()
            if (files.files.size == 0) Log.e("@@@", "No DB file exists in Drive")
            for (file in files.files) {
                Log.e("@@@", "Found file: ${file.name}, ${file.id}, ${file.createdTime}, ${file.size}")
                if (file.name.equals("Transaction")) {
                    val outputStream: OutputStream = FileOutputStream(dbPath)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                }else if(file.getName().equals("Transaction-shm")){
                    val outputStream: OutputStream = FileOutputStream(dbPathShm)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                }
                else if(file.getName().equals("Transaction-wal")){
                    val outputStream: OutputStream = FileOutputStream(dbPathWal)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                }
            }
                Log.e("@@@", "Found file: ${children}")
            }
        } catch (e: IOException) {
            Log.w("@@@","error:"+e.message.toString())
        }
    }


}