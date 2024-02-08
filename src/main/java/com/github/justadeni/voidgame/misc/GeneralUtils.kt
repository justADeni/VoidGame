package com.github.justadeni.voidgame.misc

import org.bukkit.command.CommandSender

object GeneralUtils {

    fun String.sendTo(sender: CommandSender) {
        sender.sendMessage(this)
    }

}