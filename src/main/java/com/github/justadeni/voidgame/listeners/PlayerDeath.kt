package com.github.justadeni.voidgame.listeners

import com.github.justadeni.voidgame.arena.Arenas
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath: Listener {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val arena = Arenas.ofPlayer(e.entity) ?: return
        arena.checkSurvivors()
    }

}