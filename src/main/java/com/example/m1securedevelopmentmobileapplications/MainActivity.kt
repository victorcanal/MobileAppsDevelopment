package com.example.m1securedevelopmentmobileapplications

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var cancellationSignal:CancellationSignal?=null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
    get()=
        @RequiresApi(Build.VERSION_CODES.P)
        object :BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication error:$errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Authentication success!")
                val intent = Intent(this@MainActivity, AccountDisplay::class.java)
                startActivity(intent)
            }
        }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkBiometricSupport()
        val button1: Button=findViewById(R.id.connectionButton)
        button1.setOnClickListener{
            val biometricPrompt:BiometricPrompt=BiometricPrompt.Builder(this)
                    .setTitle("Authentication")
                    .setSubtitle("To your accounts")
                    .setDescription("Allow you to keep your data safe")
                    .setNegativeButton("Cancel",this.mainExecutor, { _, _ ->
                        notifyUser("Authentication cancelled")
                    }).build()
            biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallback)
        }
    }

    private fun getCancellationSignal(): CancellationSignal{
        cancellationSignal= CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }
    private fun notifyUser(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun checkBiometricSupport():Boolean{
        val keyguardManager:KeyguardManager=getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(!keyguardManager.isKeyguardSecure)
        {
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true
    }
}