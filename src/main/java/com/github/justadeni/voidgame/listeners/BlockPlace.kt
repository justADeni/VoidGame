package com.github.justadeni.voidgame.listeners

import com.github.justadeni.voidgame.arena.Arenas
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlace: Listener {

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val arena = Arenas.ofPlayer(e.player) ?: return
        arena.placedBlocks.add(e.blockPlaced.location)
    }

}