package com.github.justadeni.voidgame.invitations

import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.misc.GeneralUtils.sendTo
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object Invitations {

    private val invitations = ConcurrentHashMap<Player, Arena.ArenaBuilder>()

    fun contains(player: Player) = invitations.containsKey(player)

    fun add(player: Player, arenaBuilder: Arena.ArenaBuilder) {
        invitations[player] = arenaBuilder
        Scopes.supervisorScope.launch {
            delay(Config.int("config.invitation-timeout").toLong())
            if (invitations.remove(player) != null) {
                "&cInvitation to ${player.name} timed out".sendTo(arenaBuilder.players[0])
                "&cInvitation from ${arenaBuilder.players[0].name} timed out.".sendTo(player)
            }
        }
    }

    fun accept(player: Player) {
        if (invitations.containsKey(player)) {
            val arenabuilder = invitations.remove(player) ?: return
            arenabuilder.players.add(player)
            "${player.name} joined game queue.".sendTo(arenabuilder.players[0])
            "Joined the game queue.".sendTo(player)
        } else {
            "Invalid invitation.".sendTo(player)
        }
    }

    fun remove(arenaBuilder: Arena.ArenaBuilder) =
        invitations.keys.removeAll(invitations
            .filterValues { it == arenaBuilder }
            .map { it.key }
            .toSet())

}