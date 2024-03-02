package com.github.justadeni.voidgame.listeners

import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.config.Config
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMove: Listener {

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (e.to == null)
            return

        if (e.to!!.y < e.from.y) {
            val pheight = Config.int("config.defaults.height.max")
            val starty = Config.int("config.defaults.start-y")
            if (e.to!!.y < starty - pheight) {
                val arena = Arenas.ofPlayer(e.player) ?: return
                val participant = arena.participants.firstOrNull { it.player == e.player } ?: return
                participant.player.gameMode = GameMode.SPECTATOR
                arena.announce(Arena.Event.DIE, e.player)
            }
        }
    }

}