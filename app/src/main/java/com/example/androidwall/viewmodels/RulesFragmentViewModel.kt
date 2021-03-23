package com.example.androidwall.viewmodels

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidwall.models.FirewallMode
import com.example.androidwall.models.Rule
import com.example.androidwall.models.RuleSet
import com.example.androidwall.utils.IPTablesHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class RulesFragmentViewModel : ViewModel(){
    private val _Packages: MutableLiveData<RuleSet> = MutableLiveData()
    val Packages: LiveData<RuleSet>
        get() = _Packages

    lateinit var context: Context

    private val ruleFile by lazy{
        File(context.filesDir, "rules.json")
    }

    fun QueryAllPackages() {
        val list : List<Rule>
        var mode = FirewallMode.WHITELIST

        if (ruleFile.exists()) {
            //Read JSON
            val json = ruleFile.readText()

            val gson = Gson()

            val type = object : TypeToken<RuleSet>() {}.type

            //Get rules from JSON
            val ruleSet : RuleSet = gson.fromJson(json, type)

            //Set the current mode
            mode = ruleSet.mode

            //Get all packages with default rules based on mode
            list = getPackages(mode)

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
        } else {
            //Get all packages with default rules based on mode
            list = getPackages(mode)

            //If it's the first run just use a clean ruleset
            saveSettings(RuleSet(mode, list))
        }

        //Set the packages
        _Packages.value = RuleSet(mode, list)
    }

    private fun getPackages(mode : FirewallMode) : List<Rule>{
        //Retrieve all installed packages
        val packages: List<PackageInfo> = context.packageManager.getInstalledPackages(
            PackageManager.GET_META_DATA
        )

        val list : MutableList<Rule> = mutableListOf()

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

    fun saveSettings(ruleSet : RuleSet) {
        val gson = Gson()

        _Packages.value = ruleSet

        //Generate JSON from rules
        val json = gson.toJson(ruleSet)

        //Write to rules.json
        ruleFile.writeText(json)

        IPTablesHelper.applyRuleset(Packages.value!!)
    }

    fun toggleMode(){
        val rules : RuleSet = _Packages.value!!

        when(rules.mode){
            FirewallMode.WHITELIST -> rules.mode = FirewallMode.BLACKLIST
            FirewallMode.BLACKLIST -> rules.mode = FirewallMode.WHITELIST
        }

        //Invert all values so the user can just continue his setup
        for (rule in rules.rules){
            rule.wifiEnabled = !rule.wifiEnabled
            rule.cellularEnabled = !rule.cellularEnabled
            rule.vpnEnabled = !rule.vpnEnabled
        }

        _Packages.value = rules
    }
}