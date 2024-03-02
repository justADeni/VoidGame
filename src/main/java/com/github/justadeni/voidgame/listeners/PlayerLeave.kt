package com.github.justadeni.voidgame.listeners

import com.github.justadeni.voidgame.arena.Arenas
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.arena.ArenaBuilders
import com.github.justadeni.voidgame.misc.GeneralUtils.sendTo

class PlayerLeave: Listener {

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        val arenabuilder = ArenaBuilders.ofPlayer(e.player)
        if (arenabuilder != null) {
            if (arenabuilder.players[0] == e.player) {
                arenabuilder.cancel()
            } else {
                arenabuilder.players.remove(e.player)
                arenabuilder.players.forEach { "${e.player.name} left the queue.".sendTo(it) }
            }
        } else {
            Arenas.ofPlayer(e.player)?.announce(Arena.Event.LEAVE, e.player)
        }
    }

}