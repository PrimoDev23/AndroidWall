package com.example.androidwall.utils

import com.example.androidwall.models.Log
import com.example.androidwall.models.RuleSet
import java.io.DataOutputStream
import java.io.OutputStream
import java.net.Inet6Address
import java.net.NetworkInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf

object IPTablesHelper {

    //Currently we only have whitelist mode and selected all by default
    //This is for testing purposes but will change later
    fun applyRuleset(ruleset: List<RuleSet>) {
        //Get a root shell
        val shell = Runtime.getRuntime().exec("su")

        //Flush the current iptables
        runCommand(shell.outputStream, "iptables -F OUTPUT")

        Logger.writeLog("iptables -F OUTPUT");

        for (rule in ruleset) {
            var command : String

            //Block all wifi connections
            if (!rule.wifiEnabled) {
                command = "iptables -A OUTPUT -o wlan+ -m owner --uid-owner ${rule.uid} -j REJECT"
                runCommand(
                    shell.outputStream,
                    command
                )
                Logger.writeLog(command)
            }

            //Block all cellular connections
            if (!rule.cellularEnabled) {
                command = "iptables -A OUTPUT -o rmnet+ -m owner --uid-owner ${rule.uid} -j REJECT"
                runCommand(
                    shell.outputStream,
                    command
                )
                Logger.writeLog(command)
            }

            //Block all vpn connections
            if (!rule.vpnEnabled) {
                command = "iptables -A OUTPUT -o tun+ -m owner --uid-owner ${rule.uid} -j REJECT"
                runCommand(
                    shell.outputStream,
                    command
                )
                Logger.writeLog(command)
            }
        }

        runCommand(shell.outputStream, "exit")
    }

    private fun runCommand(os: OutputStream, command: String) {
        os.write((command + "\n").toByteArray())
        os.flush()
    }
}