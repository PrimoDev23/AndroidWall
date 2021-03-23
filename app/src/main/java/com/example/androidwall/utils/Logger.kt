package com.example.androidwall.utils

import android.content.Context
import com.example.androidwall.models.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    lateinit var context : Context

    private val logFile by lazy {
        File(context.filesDir, "${LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.log")
    }

    private val gson by lazy {
        Gson()
    }

    fun writeLog(message : String) : MutableList<Log> {
        val logs = readLogs()

        logs.add(Log(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")), message))

        logFile.writeText(gson.toJson(logs))

        return logs
    }

    fun readLogs() : MutableList<Log>{
        if(logFile.exists()) {
            var json = logFile.readText()

            val type = object : TypeToken<List<Log>>() {}.type

            return gson.fromJson<MutableList<Log>>(json, type)
        }
        return mutableListOf()
    }

}