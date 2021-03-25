package com.example.androidwall.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rule(
    @PrimaryKey
    val name: String,
    val uid: Int,
    var wifiEnabled: Boolean,
    var cellularEnabled: Boolean,
    var vpnEnabled: Boolean
)