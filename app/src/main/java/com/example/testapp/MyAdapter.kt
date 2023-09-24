package com.example.testapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.models.UniData


class MyAdapter(private val data: List<UniData>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>()  {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{

        fun onItemClick(position: Int){

        }
    }

    fun setOnItemClickListener(listener:onItemClickListener)
    {
        mListener=listener
    }


    class MyViewHolder(val view: View,listener:onItemClickListener): RecyclerView.ViewHolder(view){

        init{
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(uniData: UniData){
//            val tv = view.findViewById<TextView>(R.id.list_tv)
//            tv.text = text
            val title = view.findViewById<TextView>(R.id.txt2)
            title.text = uniData.name

            val num = view.findViewById<TextView>(R.id.txt1)
            num.text=(adapterPosition+1).toString()

            val country = view.findViewById<TextView>(R.id.txt3)
            country.text=uniData.country

            val web = view.findViewById<TextView>(R.id.txt4)
            web.text=uniData.web[0]
//            description.text = property.description
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(v,mListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

}