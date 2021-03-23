package com.example.androidwall.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidwall.R
import com.example.androidwall.models.RuleSet

class AppListAdapter(var ruleSets : List<RuleSet>, val context : Context) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    inner class AppListViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val txt_name : TextView = view.findViewById(R.id.txt_name)
        val txt_uid : TextView = view.findViewById(R.id.txt_uid)
        val img_app : ImageView = view.findViewById(R.id.img_app)
        val chk_wifi : CheckBox = view.findViewById(R.id.chk_wifi)
        val chk_cellular : CheckBox = view.findViewById(R.id.chk_cellular)
        val chk_vpn : CheckBox = view.findViewById(R.id.chk_vpn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.applistlayout, parent, false)

        return AppListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ruleSets.size
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val pack : RuleSet = ruleSets[position]

        //Init the views
        holder.txt_name.text = pack.name
        holder.txt_uid.text = "UID: ${pack.uid}"
        holder.img_app.setImageDrawable(context.packageManager.getApplicationIcon(pack.name))
        holder.chk_wifi.isChecked = pack.wifiEnabled
        holder.chk_cellular.isChecked = pack.cellularEnabled
        holder.chk_vpn.isChecked = pack.vpnEnabled

        holder.chk_wifi.setOnClickListener {
            ruleSets[position].wifiEnabled = holder.chk_wifi.isChecked
        }

        holder.chk_cellular.setOnClickListener {
            ruleSets[position].cellularEnabled = holder.chk_cellular.isChecked
        }

        holder.chk_vpn.setOnClickListener {
            ruleSets[position].vpnEnabled = holder.chk_vpn.isChecked
        }
    }

}