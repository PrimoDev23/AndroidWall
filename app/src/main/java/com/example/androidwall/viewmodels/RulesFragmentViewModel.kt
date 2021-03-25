package com.example.androidwall.viewmodels

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidwall.models.FirewallMode
import com.example.androidwall.models.Rule
import com.example.androidwall.models.Setting
import com.example.androidwall.utils.AppDatabase
import com.example.androidwall.utils.IPTablesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RulesFragmentViewModel : ViewModel() {
    private val _Rules: MutableLiveData<List<Rule>> = MutableLiveData(listOf())
    val Rules: LiveData<List<Rule>>
        get() = _Rules

    private val _Settings: MutableLiveData<Setting> = MutableLiveData()
    val Settings: LiveData<Setting>
        get() = _Settings

    lateinit var context: Context

    private val ruleFile by lazy {
        File(context.filesDir, "rules.json")
    }

    fun initValues() {
        val setting: Setting

        val settingDao = AppDatabase.getInstance(context).settingDao()
        val rulesDao = AppDatabase.getInstance(context).rulesDao()

        val dbSettings = settingDao.getSetting()

        //If settings are not created yet we need to create a new one, else load old ones
        if (dbSettings.isNotEmpty()) {
            setting = dbSettings[0]
        } else {
            setting = Setting(0, false, FirewallMode.WHITELIST)
            settingDao.insertSetting(setting)
        }
        _Settings.postValue(setting)

        val loadedRules = rulesDao.getRules()

        val defaultRules = getPackages(setting.mode)

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

        //Set the current rules to default rules
        _Rules.postValue(defaultRules)
    }

    private fun getPackages(mode: FirewallMode): List<Rule> {
        //Retrieve all installed packages
        val packages: List<PackageInfo> = context.packageManager.getInstalledPackages(
            PackageManager.GET_META_DATA
        )

        val list: MutableList<Rule> = mutableListOf()

        val default = mode == FirewallMode.WHITELIST;

        //Get all packages with corresponding uids
        for (pack in packages) {
            val info = pack.applicationInfo
            if (info.packageName != null && info.enabled) {
                list.add(Rule(info.packageName, info.uid, default, default, default))
            }
        }

        return list
    }

    fun updateRules(rules: List<Rule>) {
        for (rule in rules) {
            AppDatabase.getInstance(context).rulesDao().updateRule(rule)
        }

        _Rules.postValue(rules)

        IPTablesHelper.applyRuleset(rules, _Settings.value!!)
    }

    fun toggleMode() {
        val setting = _Settings.value!!

        val rules = _Rules.value!!

        setting.mode =
            if (setting.mode == FirewallMode.WHITELIST) FirewallMode.BLACKLIST else FirewallMode.WHITELIST

        //Invert all settings to keep the users setup
        for (rule in rules){
            rule.wifiEnabled = !rule.wifiEnabled
            rule.cellularEnabled = !rule.cellularEnabled
            rule.vpnEnabled = !rule.vpnEnabled
        }

        //Update the rules in DB
        for (rule in rules) {
            AppDatabase.getInstance(context).rulesDao().updateRule(rule)
        }

        _Rules.postValue(rules)

        updateSetting(setting)
    }

    fun toggleEnabled() {
        val setting = _Settings.value!!

        setting.enabled = !setting.enabled

        updateSetting(setting)
    }

    private fun updateSetting(setting: Setting) {
        AppDatabase.getInstance(context).settingDao().updateSetting(setting)

        _Settings.postValue(setting)
    }
}