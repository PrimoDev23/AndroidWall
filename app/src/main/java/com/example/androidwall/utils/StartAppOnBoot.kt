package com.example.androidwall.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.example.androidwall.MainActivity
import com.example.androidwall.models.FirewallMode
import com.example.androidwall.models.Rule
import com.example.androidwall.models.RuleSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


class StartAppOnBoot : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Logger.context = p0!!
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {
            applyRules(p0)
        }
    }

    fun applyRules(context: Context) {
        val list: List<Rule>

        val ruleFile = File(context.filesDir, "rules.json")

        if (ruleFile.exists()) {
            //Read JSON
            val json = ruleFile.readText()

            val gson = Gson()

            val type = object : TypeToken<RuleSet>() {}.type

            //Get rules from JSON
            val ruleSet: RuleSet = gson.fromJson(json, type)

            //Get all packages with default rules based on mode
            list = getPackages(ruleSet.mode, context)

            //Iterate through apps and adjust rules
            //This way we using the current package list as base
            //So there won't be issues when installing/uninstalling apps
            for (r in ruleSet.rules) {
                val pack = list.firstOrNull { it.name == r.name }
                pack?.let {
                    pack.cellularEnabled = r.cellularEnabled
                    pack.wifiEnabled = r.wifiEnabled
                    pack.vpnEnabled = r.vpnEnabled
                }
            }

            IPTablesHelper.applyRuleset(RuleSet(ruleSet.enabled, ruleSet.mode, list))
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