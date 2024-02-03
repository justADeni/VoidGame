package com.github.justadeni.voidgame.listeners

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
            val pheight = Config.int("pillar.height")
            val starty = Config.int("pillar.start-y")
            if (e.to!!.y < starty - pheight) {
                val arena = Arenas.ofPlayer(e.player) ?: return
                val participant = arena.participants.firstOrNull { it.player == e.player } ?: return
                participant.player.gameMode = GameMode.SPECTATOR
            }
        }
    }

}