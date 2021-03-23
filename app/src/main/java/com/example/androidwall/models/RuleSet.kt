package com.example.androidwall.models

data class RuleSet(var enabled : Boolean, var mode : FirewallMode, var rules : List<Rule>)