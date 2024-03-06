package com.github.justadeni.voidgame.command

import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.arena.ArenaBuilders
import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.gui.AdjustGUI
import com.github.justadeni.voidgame.invitations.Invitations
import com.github.justadeni.voidgame.misc.GeneralUtils.sendTo
import com.github.justadeni.voidgame.misc.Rank
import com.github.justadeni.voidgame.misc.Status
import com.github.justadeni.voidgame.misc.StatusAndRank
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Command: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size == 0) {
            Config.string("config.messages.command.invalid-args").sendTo(sender)
            return true
        }
        if (args[0].lowercase() == "reload") {
            if (sender is ConsoleCommandSender || sender.hasPermission("voidgame.reload")) {
                Config.string("config.messages.command.reloaded").sendTo(sender)
                Config.reload()
            } else {
                Config.string("config.messages.command.no-permission").sendTo(sender)
            }
            return true
        }
        if (sender !is Player) {
            Config.string("config.messages.command.console").sendTo(sender)
            return true
        }

        val (status, rank) = StatusAndRank.get(sender)

        when (args[0].lowercase()) {
            "troll" -> {
                if (!Config.bool("config.troll.enabled")) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val arena = Arenas.ofPlayer(sender)
                if (arena == null || arena.participants[0].player != sender) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                if (args.size < 2) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val player = Bukkit.getPlayer(args[1])
                if (player == null || !arena.participants.any { it.player == player }) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val participant = arena.participant(player)
                if (participant == null) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                if (participant.trollmode) {
                    "Disabled troll mode for ${player.name}."
                } else {
                    "Enabled troll mode for ${player.name}."
                }.sendTo(sender)

                participant.trollmode = !participant.trollmode
            }
            "create" -> {
                if (status != Status.FREE) {
                    "Cannot create an arena.".sendTo(sender)
                    return true
                }

                val arenabuilder = Arena.ArenaBuilder()
                arenabuilder.players.add(sender)
                ArenaBuilders.add(arenabuilder)
                "Arena created. Invite players using <&n/vg invite or click here>(-vg invite ). </vg setup or click here to setup.>(/vg setup)".sendTo(sender)
            }
            "invite" -> {
                if (status != Status.FREE || rank != Rank.LEADER) {
                    "Cannot invite.".sendTo(sender)
                    return true
                }
                if (args.size < 2) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val player = Bukkit.getPlayer(args[1])
                if (player == null) {
                    Config.string("config.messages.command.invalid-args").sendTo(sender)
                    return true
                }
                val inviteearena = ArenaBuilders.ofPlayer(player)
                if (Invitations.contains(player) || inviteearena != null) {
                    "Cannot invite ${player.name}.".sendTo(sender)
                    return true
                }
                "${sender.name} invited you to a VoidGame. <&n/vg accept or click here>(/vg accept) to accept.".sendTo(player)
                Invitations.add(player, ArenaBuilders.ofPlayer(sender)!!)
            }
            "accept" -> {
                if (!Invitations.contains(sender)) {
                    "You don't have an active invite.".sendTo(sender)
                    return true
                }
                Invitations.accept(sender)
            }
            "leave" -> {
                val arenabuilder = ArenaBuilders.ofPlayer(sender)
                if (arenabuilder == null) {
                    "You aren't in any queue".sendTo(sender)
                    return true
                }
                if (arenabuilder.players[0] == sender) {
                    "You can't leave your own queue. Use <&n/vg cancel>(/vg cancel) instead".sendTo(sender)
                    return true
                }
                arenabuilder.players.remove(sender)
                "You left the queue".sendTo(sender)
                "${sender.name} left the queue".sendTo(arenabuilder.players[0])
            }
            "cancel" -> {
                val arenabuilder = ArenaBuilders.ofPlayer(sender)
                if (arenabuilder == null) {
                    "You aren't in any queue".sendTo(sender)
                    return true
                }
                if (arenabuilder.players[0] != sender) {
                    "You aren't the leader of this queue.".sendTo(sender)
                    return true
                }
                arenabuilder.cancel()
                "Queue disbanded.".sendTo(sender)
            }
            "setup" -> {
                val arenabuilder = ArenaBuilders.ofPlayer(sender)
                if (arenabuilder == null) {
                    "You aren't in any queue".sendTo(sender)
                    return true
                }
                if (arenabuilder.players[0] != sender) {
                    "You aren't the leader of this queue.".sendTo(sender)
                    return true
                }
                AdjustGUI(sender, arenabuilder).open()
            }
        }
        return true
    }

}