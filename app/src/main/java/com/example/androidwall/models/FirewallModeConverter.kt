package com.example.androidwall.models

import androidx.room.TypeConverter

class FirewallModeConverter {
    @TypeConverter
    fun toFirewallMode(value : Int) = enumValues<FirewallMode>()[value]

    @TypeConverter
    fun fromFirewallMode(mode : FirewallMode) = mode.ordinal
}