package com.github.justadeni.voidgame.listeners

import com.github.justadeni.voidgame.arena.Arenas
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawn: Listener {

    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        val arena = Arenas.ofPlayer(e.player) ?: return
        e.respawnLocation = arena.arenaworld.respawnloc
    }

}