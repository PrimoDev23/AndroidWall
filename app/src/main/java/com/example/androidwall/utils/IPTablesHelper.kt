package com.example.androidwall.utils

import com.example.androidwall.models.FirewallMode
import com.example.androidwall.models.RuleSet
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.util.concurrent.TimeUnit

object IPTablesHelper {

    private val CHAINNAME = "AndroidWallChain"

    //Currently we only have whitelist mode and selected all by default
    //This is for testing purposes but will change later
    fun applyRuleset(ruleset: RuleSet) {
        //Get a root shell
        val shell = Runtime.getRuntime().exec("su")

        //Only apply rules if firewall is not disabled
        if (ruleset.enabled) {
            //We are always setting up the chain again since we can't really check for errors
            //If operation succeed error stream can't be read
            setupChain(shell)

            if (ruleset.mode == FirewallMode.WHITELIST) {
                //Default to drop for whitelist
                runCommand(shell, "iptables -P OUTPUT DROP")

                //Set some base rules (those are needed to make internet work for whitelisted apps)
                runCommand(shell, "iptables -A $CHAINNAME -m owner --uid-owner wifi -j ACCEPT")
                runCommand(shell, "iptables -A $CHAINNAME -m owner --uid-owner dhcp -j ACCEPT")
                runCommand(shell, "iptables -A $CHAINNAME -m owner --uid-owner root -j ACCEPT")

                //Accept all outgoing connections to loopback
                runCommand(shell, "iptables -A OUTPUT -o lo -j ACCEPT")
            } else {
                //Default to accept on blacklist
                runCommand(shell, "iptables -P OUTPUT ACCEPT")
            }

            val policy = if (ruleset.mode == FirewallMode.WHITELIST) "ACCEPT" else "DROP"

            for (rule in ruleset.rules) {
                //Block all wifi connections
                if (rule.wifiEnabled) {
                    runCommand(
                        shell,
                        "iptables -A $CHAINNAME -o wlan+ -m owner --uid-owner ${rule.uid} -j $policy"
                    )
                }

                //Block all cellular connections
                if (rule.cellularEnabled) {
                    runCommand(
                        shell,
                        "iptables -A $CHAINNAME -o rmnet+ -m owner --uid-owner ${rule.uid} -j  $policy"
                    )
                }

                //Block all vpn connections
                if (rule.vpnEnabled) {
                    runCommand(
                        shell,
                        "iptables -A $CHAINNAME -o tun+ -m owner --uid-owner ${rule.uid} -j  $policy"
                    )
                }
            }
        } else {
            cleanupChain(shell)
        }

        runCommand(shell, "exit")
    }

    private fun setupChain(shell: Process) {
        //Remove the chain from output if possible
        runCommand(shell, "iptables -D OUTPUT -j AndroidWallChain")

        //Create our chain
        runCommand(shell, "iptables -N $CHAINNAME")

        //Always flush here
        runCommand(shell, "iptables -F $CHAINNAME")

        //Connect output to our chain
        runCommand(shell, "iptables -A OUTPUT -j $CHAINNAME")
    }

    private fun cleanupChain(shell: Process) {
        //Remove the chain from output if possible
        runCommand(shell, "iptables -D OUTPUT -j AndroidWallChain")

        //Remove the chain
        runCommand(shell, "iptables -X $CHAINNAME")

        //Accept all connections in output (default)
        runCommand(shell, "iptables -P OUTPUT ACCEPT")
    }

    private fun runCommand(shell: Process, command: String) {
        Logger.writeLog(command)
        shell.outputStream.write((command + "\n").toByteArray())
        shell.outputStream.flush()
    }
}