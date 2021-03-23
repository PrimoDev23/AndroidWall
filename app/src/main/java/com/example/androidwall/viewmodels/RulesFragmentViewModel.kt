package com.example.androidwall.viewmodels

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidwall.models.RuleSet
import com.example.androidwall.utils.IPTablesHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class RulesFragmentViewModel : ViewModel(){
    private val _Packages: MutableLiveData<List<RuleSet>> = MutableLiveData()
    val Packages: LiveData<List<RuleSet>>
        get() = _Packages

    lateinit var context: Context

    private val ruleFile by lazy{
        File(context.filesDir, "rules.json")
    }

    fun setPackages(ruleSets: List<RuleSet>) {
        _Packages.value = ruleSets
    }

    fun QueryAllPackages() {
        //Create a list to store packages
        val list: MutableList<RuleSet> = mutableListOf()

        //Retrieve all installed packages
        val packages: List<PackageInfo> = context.packageManager.getInstalledPackages(
            PackageManager.GET_META_DATA
        )

        //Use a default variable since we are going to add blacklist mode soon
        val default : Boolean = true

        //Get all packages with corresponding uids
        for (pack in packages) {
            val info = pack.applicationInfo
            if (info.name != null && info.enabled) {
                list.add(RuleSet(info.packageName, info.uid, default, default, default))
            }
        }

        if (ruleFile.exists()) {
            //Read JSON
            val json = ruleFile.readText()

            val gson = Gson()

            val type = object : TypeToken<List<RuleSet>>() {}.type

            //Get rules from JSON
            val rules : List<RuleSet> = gson.fromJson(json, type)

            //Iterate through apps and adjust rules
            //This way we using the current package list as base
            //So there won't be issues when installing/uninstalling apps
            for (r in rules) {
                val pack = list.firstOrNull { it.name == r.name }
                pack?.let {
                    pack.cellularEnabled = r.cellularEnabled
                    pack.wifiEnabled = r.wifiEnabled
                    pack.vpnEnabled = r.vpnEnabled
                }
            }
        } else {
            //If it's the first run just use a clean ruleset
            saveSettings(list)
        }

        //Set the packages
        _Packages.value = list
    }

    fun saveSettings(ruleSet : List<RuleSet>) {
        val gson = Gson()

        _Packages.value = ruleSet

        //Generate JSON from rules
        val json = gson.toJson(ruleSet)

        //Write to rules.json
        ruleFile.writeText(json)

        IPTablesHelper.applyRuleset(Packages.value!!)
    }
}