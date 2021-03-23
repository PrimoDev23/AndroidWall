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

        //Only apply rules if firewall is not disabled
        if(ruleset.enabled) {
            //Flush the current iptables
            runCommand(shell.outputStream, "iptables -F OUTPUT")

            if (ruleset.mode == FirewallMode.WHITELIST) {
                //Default to drop for whitelist
                runCommand(
                    shell.outputStream,
                    "iptables -P OUTPUT DROP"
                )
            } else {
                //Default to accept on blacklist
                runCommand(
                    shell.outputStream,
                    "iptables -P OUTPUT ACCEPT"
                )
            }

            val policy = if (ruleset.mode == FirewallMode.WHITELIST) "ACCEPT" else "DROP"

            //Accept all outgoing connections to loopback
            runCommand(shell.outputStream, "iptables -A OUTPUT -o lo -j ACCEPT")

            for (rule in ruleset.rules) {
                //Block all wifi connections
                if (rule.wifiEnabled) {
                    runCommand(
                        shell.outputStream,
                        "iptables -A OUTPUT -o wlan+ -m owner --uid-owner ${rule.uid} -j $policy"
                    )
                }

                //Block all cellular connections
                if (rule.cellularEnabled) {
                    runCommand(
                        shell.outputStream,
                        "iptables -A OUTPUT -o rmnet+ -m owner --uid-owner ${rule.uid} -j  $policy"
                    )
                }

                //Block all vpn connections
                if (rule.vpnEnabled) {
                    runCommand(
                        shell.outputStream,
                        "iptables -A OUTPUT -o tun+ -m owner --uid-owner ${rule.uid} -j  $policy"
                    )
                }
            }
        }else{
            //Flush rules
            runCommand(shell.outputStream, "iptables -F OUTPUT")

            //Accept all connections
            runCommand(shell.outputStream, "iptables -P OUTPUT ACCEPT")
        }

        runCommand(shell.outputStream, "exit")
    }

    private fun runCommand(os: OutputStream, command: String) {
        Logger.writeLog(command)
        os.write((command + "\n").toByteArray())
        os.flush()
    }
}