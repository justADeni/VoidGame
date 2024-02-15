package com.github.justadeni.voidgame.misc

import com.github.justadeni.voidgame.VoidGame
import org.bukkit.ChatColor

object Logger {

    @JvmStatic
    internal fun log(message: String) {
        VoidGame.plugin.logger.info(message)
    }

    @JvmStatic
    internal fun warn(msg: String){
        VoidGame.plugin.logger.warning(ChatColor.translateAlternateColorCodes('&', msg))
    }

}