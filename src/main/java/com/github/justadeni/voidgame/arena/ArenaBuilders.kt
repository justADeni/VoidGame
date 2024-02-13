package com.github.justadeni.voidgame.arena

import org.bukkit.entity.Player

object ArenaBuilders {

    private val arenabuilders = mutableListOf<Arena.ArenaBuilder>()

    fun add(arena: Arena.ArenaBuilder) = arenabuilders.add(arena)

    fun has(arena: Arena.ArenaBuilder) = arenabuilders.contains(arena)

    fun remove(arena: Arena.ArenaBuilder) = arenabuilders.remove(arena)

    fun size() = arenabuilders.size

    fun get() = arenabuilders

    fun ofPlayer(player: Player): Arena.ArenaBuilder? {
        for (arena in arenabuilders) {
            for (participant in arena.players) {
                if (participant.player == player)
                    return arena
            }
        }
        return null
    }

}