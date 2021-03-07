package com.example.m1securedevelopmentmobileapplications

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.HttpsURLConnection


class AccountDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_display)
    }

    override fun onStart() {
        super.onStart()
        val accountsTextView: TextView = findViewById(R.id.accountsTextView)
        val timeUpdatedTextView : TextView = findViewById(R.id.timeUpdatedTextView)
        val refreshButton : Button = findViewById(R.id.refreshButton)

        val url = getString(R.string.url)
        var temp = "DEFAULT"

        accountsTextView.movementMethod = ScrollingMovementMethod()

        refreshButton.setOnClickListener(){
            finish()
            startActivity(intent)
        }

        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = cm.activeNetwork

        try{
            //accountsTextView.text = readingFile()
            //temp = openFileInput("data.txt").toString()
            temp = readFileOnInternalStorage("data.txt")
        }
        catch (e: Exception){
            println("there is no data.txt file")
        }

        if(cm.getNetworkCapabilities(activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            GlobalScope.launch {
                temp = dataRetrieval(url)

                val array = JSONArray(temp)
                temp = ""
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    temp += obj.toString(4) + "\n"
                }
                runOnUiThread { timeUpdatedTextView.text = Calendar.HOUR_OF_DAY.toString() }
                accountsTextView.text = temp
                //savingFile(temp)
            }
        }
        //val stream : FileOutputStream = openFileOutput("data.txt", MODE_PRIVATE)
        //stream.write(temp.toByteArray())
        //stream.flush()
        //stream.close()
        writeFileOnInternalStorage("data.txt",temp)
    }

    override fun onRestart() {
        super.onRestart()
        val intent = Intent(this@AccountDisplay, MainActivity::class.java)
        startActivity(intent)
    }

    fun writeFileOnInternalStorage(filename: String, body: String) {
        val dir = File(filesDir, "data")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val file = File(dir, filename)
            val writer = FileWriter(file)
            writer.append(body)
            writer.flush()
            writer.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun readFileOnInternalStorage(filename: String) : String{
        val dir = File(filesDir, "data")
        var result = ""
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val file = File(dir, filename)
            val reader = FileReader(file)
            result = reader.toString()
            reader.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }


    private fun dataRetrieval(url: String): String {
        val urlAddress = URL(url)
        val httpsClient = urlAddress.openConnection() as HttpsURLConnection

        if (httpsClient.responseCode == HttpsURLConnection.HTTP_OK) {
            try {
                val stream = BufferedInputStream(httpsClient.inputStream)
                return readStream(inputStream = stream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpsClient.disconnect()
            }
        } else {
            return "ERROR ${httpsClient.responseCode}"
        }
        return "NULL"
    }

    private fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    private fun savingFile(text: String){
        val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        val dir : File = filesDir
        val file = File("data.txt")
        file.createNewFile()

        val fileToWrite = "data.txt"
        val encryptedFile = EncryptedFile.Builder(
                applicationContext,
                File(dir, fileToWrite),
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        //val stream: FileOutputStream = openFileOutput("data.txt", MODE_PRIVATE)
        val fileContent = text
                .toByteArray(StandardCharsets.UTF_8)
        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }
    }

    private fun readingFile(): String {
        val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        val dir: File = filesDir
        val fileToRead = "data.txt"
        val encryptedFile = EncryptedFile.Builder(
                applicationContext,
                File(dir, fileToRead),
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val inputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        return byteArrayOutputStream.toByteArray().toString()
    }
}