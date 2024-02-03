package com.github.justadeni.voidgame.arena

import org.bukkit.entity.Player

object Arenas {

    private val arenas = mutableListOf<Arena>()

    fun add(arena: Arena) = arenas.add(arena)

    fun has(arena: Arena) = arenas.contains(arena)

    fun remove(arena: Arena) = arenas.remove(arena)

    fun size() = arenas.size

    fun get() = arenas

    fun ofPlayer(player: Player): Arena? {
        for (arena in arenas) {
            for (participant in arena.participants) {
                if (participant.player == player)
                    return arena
            }
        }
        return null
    }

}