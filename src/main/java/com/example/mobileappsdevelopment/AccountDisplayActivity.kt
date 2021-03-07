package com.example.mobileappsdevelopment

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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.*
import java.net.URL
import java.text.DateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

class AccountDisplayActivity : AppCompatActivity() {
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
        var temp: String
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = cm.activeNetwork

        accountsTextView.movementMethod = ScrollingMovementMethod()
        refreshButton.setOnClickListener {
            finish()
            startActivity(intent)
        }
        try{
            accountsTextView.text = readFileOnInternalStorage("data")
            timeUpdatedTextView.text = readFileOnInternalStorage("time")
        }
        catch (e: Exception){}
        if(cm.getNetworkCapabilities(activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            GlobalScope.launch {
                temp = dataRetrieval(url)

                val array = JSONArray(temp)
                temp = ""
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    temp += obj.toString(4) + "\n"
                }

                val time = DateFormat.getDateTimeInstance().format(Date())
                runOnUiThread { timeUpdatedTextView.text = time }
                accountsTextView.text = temp
                try {
                    writeFileOnInternalStorage("data", temp)
                    writeFileOnInternalStorage("time", time)
                }
                catch (e: Exception){}
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        val intent = Intent(this@AccountDisplayActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        this.finish()
    }

    private fun writeFileOnInternalStorage(filename: String, body: String) {
        val dir = File(applicationContext.filesDir, "data")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val file = File(dir, filename)
            file.delete()
            val writer : FileWriter = FileWriter(file)
            writer.write(body)
            writer.flush()
            writer.close()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun readFileOnInternalStorage(filename: String) : String{
        val dir = File(applicationContext.filesDir, "data")
        var result = ""
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val file = File(dir, filename)
            val reader = FileReader(file)
            result = reader.readText()
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
}