package com.example.androidwall.models

data class RuleSet(val name : String, val uid : Int, var wifiEnabled : Boolean, var cellularEnabled : Boolean, var vpnEnabled : Boolean)