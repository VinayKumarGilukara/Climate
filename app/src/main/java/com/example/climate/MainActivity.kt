package com.example.climate

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CallAPILoginAsyncTask().execute()
    }
    private inner class CallAPILoginAsyncTask(): AsyncTask<Any, Void, String>(){
        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
         var result:String
         var connection:HttpURLConnection? = null
            try{
                val url = URL("https://run.mocky.io/v3/478301e0-937a-46ba-a458-ede0c789f510")
                 connection =url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                val httpResult :Int =connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line :String?

                    try{
                        while (reader.readLine().also { line = it}!=null){
                            stringBuilder.append(line + "\n")
                        }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e:SocketTimeoutException){
                result = "Connection TimeOut"
            }catch (e:Exception){
                result = "Error: " +e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()

            if (result != null) {
                Log.i("JSON RESPONSE RESULT", result)

                val jsonObject = JSONObject(result)
                val id = jsonObject.optInt("id")
                Log.i("id", id.toString())
                val name = jsonObject.optString("name")
                Log.i("name",name)
            }


        }

        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(androidx.customview.R.layout.custom_dialog)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }

    }
}