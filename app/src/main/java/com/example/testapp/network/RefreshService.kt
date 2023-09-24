package com.example.testapp.network

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.MyAdapter
import com.example.testapp.R
import com.example.testapp.WebActivity
import com.example.testapp.models.UniData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ApiCallService : Service() {

    private val apiCallInterval = TimeUnit.SECONDS.toMillis(10) // 10 seconds
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var queryWord: String // The query word received from MainActivity

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra(EXTRA_QUERY_WORD)) {
            queryWord = intent.getStringExtra(EXTRA_QUERY_WORD) ?: ""
        }
        startApiCallLoop()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun startApiCallLoop() {
        handler.post(object : Runnable {
            override fun run() {

                //Make API Call
                Api.retrofitService.getAllData(queryWord).enqueue(object: Callback<List<UniData>> {
                    override fun onResponse(
                        call: Call<List<UniData>>,
                        response: Response<List<UniData>>
                    ) {
                        if(response.isSuccessful){
                            if(response.body()?.isEmpty() == true)
                            {
                                //on err
                            }else{
                                //mark API run time
                                val currentTimeMillis = System.currentTimeMillis()
                                val intent = Intent(ACTION_API_RESPONSE)
                                val datetime=convertLongToTime(currentTimeMillis)
                                val message = "API called at: $datetime"
                                val dataTosend=response.body()!!

                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                                intent.putExtra(EXTRA_API_RUNTIME, datetime)
                                intent.putExtra("API_RESPONSE", dataTosend as Serializable)

                                sendBroadcast(intent)
                            }
                        }else{
                            //on err
                        }
                    }

                    override fun onFailure(call: Call<List<UniData>>, t: Throwable) {
                        t.printStackTrace()
                        //on err
                    }
                })

                // Schedule the next API call
                handler.postDelayed(this, apiCallInterval)
            }
        })
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm:ss   dd/MM")
        return format.format(date)
    }

    companion object {
        const val ACTION_API_RESPONSE = "com.example.testapp.network.API_RESPONSE"
        const val EXTRA_API_RUNTIME = ""
        const val EXTRA_QUERY_WORD = ""
    }
}
