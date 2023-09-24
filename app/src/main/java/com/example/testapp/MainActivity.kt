package com.example.testapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.models.UniData
import com.example.testapp.network.ApiCallService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var searchView: SearchView
    private lateinit var searchPromptView: TextView
    private lateinit var refreshTextView: TextView
    private val apiResponseReceiver = ApiResponseReceiver()

    inner class ApiResponseReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ApiCallService.ACTION_API_RESPONSE) {
                val timeval = intent.getStringExtra(ApiCallService.EXTRA_API_RUNTIME)
                val apiresp = intent.getSerializableExtra("API_RESPONSE")

                println(apiresp)

                if(apiresp!=null)
                {
                    //Update RecylerView with API Response
                    val uniresp=apiresp as MutableList<UniData>
                    val myAdapter=MyAdapter(uniresp)
                    recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                    recyclerView.layoutManager=manager
                    recyclerView.adapter=myAdapter

                    myAdapter.setOnItemClickListener(object: MyAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent= Intent(this@MainActivity,WebActivity::class.java)
                            intent.putExtra("URL",uniresp[position].web[0])
                            startActivity(intent)
                        }
                    })
                    searchPromptView.visibility=TextView.INVISIBLE
                    recyclerView.visibility=TextView.VISIBLE

                    // Update the TextView with the API run time
                    refreshTextView=findViewById<TextView>(R.id.textView2)
                    refreshTextView.text = "Last Refreshed: "+ timeval
                    refreshTextView.visibility=TextView.VISIBLE
                }


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver to prevent memory leaks
        unregisterReceiver(apiResponseReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = LinearLayoutManager(this)
        searchView = findViewById<SearchView>(R.id.search)
        searchPromptView = findViewById<TextView>(R.id.textView)
        recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        refreshTextView=findViewById<TextView>(R.id.textView2)


        refreshTextView.visibility=TextView.INVISIBLE
        searchView.clearFocus()
        val filter = IntentFilter(ApiCallService.ACTION_API_RESPONSE)
        registerReceiver(apiResponseReceiver, filter)
        val serviceIntent = Intent(this, ApiCallService::class.java)

        searchView.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                searchView.setQuery("",false);
                recyclerView.visibility=TextView.INVISIBLE
                searchPromptView.visibility=TextView.VISIBLE
                searchPromptView.text="Make a search above!"
                searchView.clearFocus()
                return false
            }
        }
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                val searchText = query!!
                if (searchText!=""){
                    searchPromptView.text="Loading..."
                    getAllData(searchText)

                    serviceIntent.putExtra(ApiCallService.EXTRA_QUERY_WORD, searchText)
                    startService(serviceIntent)

                }else{
                    searchPromptView.text="Non Empty string required!"
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchPromptView.text="Make a search above!"
                val searchText = newText!!
                if(searchText=="")
                {
                    recyclerView.visibility=TextView.INVISIBLE
                    searchPromptView.visibility=TextView.VISIBLE
                    refreshTextView.visibility=TextView.INVISIBLE
                    stopService(serviceIntent)
                }
                return false
            }


        })

    }

    fun getAllData(query:String){
        Api.retrofitService.getAllData(query).enqueue(object: Callback<List<UniData>>{
            override fun onResponse(
                call: Call<List<UniData>>,
                response: Response<List<UniData>>
            ) {
                if(response.isSuccessful){
                    if(response.body()?.isEmpty() == true)
                    {
                        recyclerView.visibility=TextView.INVISIBLE
                        searchPromptView.visibility=TextView.VISIBLE
                        searchPromptView.text="No Results found!"

                    }else{
                        val myAdapter = MyAdapter(response.body()!!)
                        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        recyclerView.layoutManager=manager
                        recyclerView.adapter=myAdapter

                        myAdapter.setOnItemClickListener(object: MyAdapter.onItemClickListener{
                            override fun onItemClick(position: Int) {
                                val intent= Intent(this@MainActivity,WebActivity::class.java)
                                intent.putExtra("URL",response.body()!![position].web[0])
                                startActivity(intent)
                            }
                        })
                        searchPromptView.visibility=TextView.INVISIBLE
                        recyclerView.visibility=TextView.VISIBLE
                    }
                }else{
                    searchPromptView.text="An Error occurred"
                    recyclerView.visibility=TextView.INVISIBLE
                }
            }

            override fun onFailure(call: Call<List<UniData>>, t: Throwable) {
                t.printStackTrace()
                searchPromptView.text="An Error occurred"
                recyclerView.visibility=TextView.INVISIBLE
            }
        })
    }
}
