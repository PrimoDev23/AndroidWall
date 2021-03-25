package com.example.androidwall.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.example.androidwall.models.FirewallMode
import com.example.androidwall.models.Rule
import com.example.androidwall.models.Setting


class StartAppOnBoot : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Logger.context = p0!!
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {
            applyRules(p0)
        }
    }

    fun applyRules(context : Context){
        val setting : Setting

        val settingDao = AppDatabase.getInstance(context).settingDao()
        val rulesDao = AppDatabase.getInstance(context).rulesDao()

        val dbSettings = settingDao.getSetting()

        //If settings are not created yet we need to create a new one, else load old ones
        if(dbSettings.isNotEmpty()) {
            setting = dbSettings[0]

            val loadedRules = rulesDao.getRules()

            val defaultRules = getPackages(setting.mode, context)

            //If rules are empty we are creating default rules for every package
            //If rules exist just load them
            if (loadedRules.isNotEmpty()) {
                for (rule in defaultRules) {
                    val match = loadedRules.firstOrNull { it.name == rule.name }

                    //If we found a match update the default rules
                    //Else use the default rule
                    if (match != null) {
                        rule.cellularEnabled = match.cellularEnabled
                        rule.wifiEnabled = match.wifiEnabled
                        rule.vpnEnabled = match.vpnEnabled
                    }
                }
            }
        }
    }

    private fun getPackages(mode: FirewallMode, context: Context): List<Rule> {
        //Retrieve all installed packages
        val packages: List<PackageInfo> = context.packageManager.getInstalledPackages(
            PackageManager.GET_META_DATA
        )

        val list: MutableList<Rule> = mutableListOf()

        val default = mode == FirewallMode.WHITELIST;

        //Get all packages with corresponding uids
        for (pack in packages) {
            val info = pack.applicationInfo
            if (info.name != null && info.enabled) {
                list.add(Rule(info.packageName, info.uid, default, default, default))
            }
        }

        return list
    }

}