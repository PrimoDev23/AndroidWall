package com.example.androidwall.utils

import com.example.androidwall.models.FirewallMode
import com.example.androidwall.models.Rule
import com.example.androidwall.models.RuleSet
import java.io.OutputStream

object IPTablesHelper {

    //Currently we only have whitelist mode and selected all by default
    //This is for testing purposes but will change later
    fun applyRuleset(ruleset : RuleSet) {
        //Get a root shell
        val shell = Runtime.getRuntime().exec("su")

        //Flush the current iptables
        runCommand(shell.outputStream, "iptables -F OUTPUT")

        //Default to drop
        runCommand(shell.outputStream, "iptables -P OUTPUT REJECT")
        runCommand(shell.outputStream, "iptables -A OUTPUT -o lo -j ACCEPT")

        for (rule in ruleset.rules) {
            var command : String

            //Block all wifi connections
            if ((ruleset.mode == FirewallMode.WHITELIST && rule.wifiEnabled) || (ruleset.mode == FirewallMode.BLACKLIST && !rule.wifiEnabled)) {
                runCommand(
                    shell.outputStream,
                    "iptables -A OUTPUT -o wlan+ -m owner --uid-owner ${rule.uid} -j ACCEPT"
                )
            }

            //Block all cellular connections
            if ((ruleset.mode == FirewallMode.WHITELIST && rule.cellularEnabled) || (ruleset.mode == FirewallMode.BLACKLIST && !rule.cellularEnabled)) {
                runCommand(
                    shell.outputStream,
                    "iptables -A OUTPUT -o rmnet+ -m owner --uid-owner ${rule.uid} -j ACCEPT"
                )
            }

            //Block all vpn connections
            if ((ruleset.mode == FirewallMode.WHITELIST && rule.vpnEnabled) || (ruleset.mode == FirewallMode.BLACKLIST && !rule.vpnEnabled)) {
                runCommand(
                    shell.outputStream,
                    "iptables -A OUTPUT -o tun+ -m owner --uid-owner ${rule.uid} -j ACCEPT"
                )
            }
        }

        runCommand(shell.outputStream, "exit")
    }

    private fun runCommand(os: OutputStream, command: String) {
        Logger.writeLog(command)
        os.write((command + "\n").toByteArray())
        os.flush()
    }
}