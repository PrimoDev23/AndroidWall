package com.example.androidwall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidwall.R
import com.example.androidwall.models.Log

class LogsAdapter(var logs : List<Log>) : RecyclerView.Adapter<LogsAdapter.LogViewHolder>(){
    inner class LogViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txt_time : TextView = view.findViewById(R.id.txt_time)
        val txt_date : TextView = view.findViewById(R.id.txt_date)
        val txt_message : TextView = view.findViewById(R.id.txt_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.loglayout, parent, false)

        return LogViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val item = logs[position]

        holder.txt_date.text = item.date
        holder.txt_time.text = item.time
        holder.txt_message.text = item.message
    }
}