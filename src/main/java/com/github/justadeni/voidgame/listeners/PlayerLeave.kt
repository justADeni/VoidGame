package com.github.justadeni.voidgame.listeners

import com.github.justadeni.voidgame.arena.Arenas
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.arena.ArenaBuilders

class PlayerLeave: Listener {

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        ArenaBuilders.ofPlayer(e.player)?.players?.remove(e.player)
        Arenas.ofPlayer(e.player)?.announce(Arena.Event.LEAVE, e.player)
    }

}