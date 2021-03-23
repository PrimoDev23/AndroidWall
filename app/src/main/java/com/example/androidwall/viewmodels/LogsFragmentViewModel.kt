package com.example.androidwall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidwall.models.Log
import com.example.androidwall.utils.Logger

class LogsFragmentViewModel : ViewModel() {
    private val _Logs : MutableLiveData<MutableList<Log>> = MutableLiveData(mutableListOf())
    val Logs : LiveData<MutableList<Log>>
        get() = _Logs

    fun readLogs(){
        _Logs.value = Logger.readLogs()
    }
}