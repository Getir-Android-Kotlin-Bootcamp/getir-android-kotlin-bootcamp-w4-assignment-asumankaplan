package com.getir.patika.week4


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginTextView: TextView = findViewById(R.id.loginTextView)
        val profileTextView: TextView = findViewById(R.id.profileTextView)

        val username = "asu"
        val password = "123"
        val userId = "c8f6a25f-7b8d-4db3-9662-3a1a24cfe927" // Örnek bir UUID

        Thread {
            try {
                // Login isteği
                val loginUrl = URL("https://espresso-food-delivery-backend-cc3e106e2d34.herokuapp.com/login")
                val loginConnection = loginUrl.openConnection() as HttpURLConnection
                loginConnection.requestMethod = "POST"
                loginConnection.setRequestProperty("Content-Type", "application/json")
                loginConnection.doOutput = true
                val loginRequestBody = """{"username": "$username", "password": "$password"}"""
                val loginOutputWriter = OutputStreamWriter(loginConnection.outputStream)
                loginOutputWriter.write(loginRequestBody)
                loginOutputWriter.flush()

                if (loginConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val loginInputStream = loginConnection.inputStream
                    val loginReader = BufferedReader(InputStreamReader(loginInputStream))
                    val loginResponse = StringBuilder()
                    var loginLine: String?
                    while (loginReader.readLine().also { loginLine = it } != null) {
                        loginResponse.append(loginLine)
                    }
                    loginReader.close()
                    runOnUiThread{
                        loginTextView.text = "Login Response: ${loginResponse.toString()}"
                    }

                    // Profil isteği
                    val profileUrl = URL("https://espresso-food-delivery-backend-cc3e106e2d34.herokuapp.com/profile/$userId")
                    val profileConnection = profileUrl.openConnection() as HttpURLConnection
                    profileConnection.requestMethod = "GET"

                    if (profileConnection.responseCode == HttpURLConnection.HTTP_OK) {
                        val profileInputStream = profileConnection.inputStream
                        val profileReader = BufferedReader(InputStreamReader(profileInputStream))
                        val profileResponse = StringBuilder()
                        var profileLine: String?
                        while (profileReader.readLine().also { profileLine = it } != null) {
                            profileResponse.append(profileLine)
                        }
                        profileReader.close()
                        runOnUiThread {
                            profileTextView.text = "Profile Response: ${profileResponse.toString()}"
                        }
                    } else {
                        println("Profile retrieval failed: ${profileConnection.responseMessage}")
                    }
                    profileConnection.disconnect()
                } else {
                    println("Login failed: ${loginConnection.responseMessage}")
                }
                loginConnection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}
