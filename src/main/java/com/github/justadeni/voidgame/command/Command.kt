package com.github.justadeni.voidgame.command

import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.misc.GeneralUtils.sendTo
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Command: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size == 0) {
            Config.string("messages.command.invalid-args").sendTo(sender)
            return true
        }
        if (args[0].lowercase() == "reload") {
            if (sender is ConsoleCommandSender || sender.hasPermission("voidgame.reload")) {
                Config.string("messages.command.reloaded").sendTo(sender)
                Config.reload()
            } else {
                Config.string("messages.command.no-permission").sendTo(sender)
            }
            return true
        }
        if (sender !is Player) {
            Config.string("messages.command.console").sendTo(sender)
            return true
        }

        when (args[0].lowercase()) {
            "troll" -> {
                if (!Config.bool("troll.enabled")) {
                    Config.string("messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val arena = Arenas.ofPlayer(sender)
                if (arena == null || arena.participants[0].player != sender) {
                    Config.string("messages.command.invalid-args").sendTo(sender)
                    return true
                }
                if (args.size < 2) {
                    Config.string("messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val player = Bukkit.getPlayer(args[1])
                if (player == null || !arena.participants.any { it.player == player }) {
                    Config.string("messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val participant = arena.participant(player)
                if (participant == null) {
                    Config.string("messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val string = if (participant.trollmode) {
                    Config.string("messages.command.untroll")
                } else {
                    Config.string("messages.command.troll")
                }
                    .replace("%player%", player.name)
                    .sendTo(sender)

                participant.trollmode = !participant.trollmode
            }
        }
        return true
    }

}