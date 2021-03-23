package com.example.androidwall.models

data class Rule(val name : String, val uid : Int, var wifiEnabled : Boolean, var cellularEnabled : Boolean, var vpnEnabled : Boolean)