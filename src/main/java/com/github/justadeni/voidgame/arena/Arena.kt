package com.github.justadeni.voidgame.arena

import com.github.justadeni.voidgame.worlds.VoidChunkGenerator
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player


class Arena private constructor(val participants: List<Participant>, pillarDist: Int, private val itemTime: Int, private val totalRounds: Int) {

    data class Participant(val beforePos: Location, val player: Player, var won: Int, var trollmode: Boolean = false)

    class ArenaBuilder() {
        val players = mutableSetOf<Player>()

        var pillarDist = 0

        var itemTime = 0

        var totalRounds = 0

        fun build(): Arena {
            val arena = Arena(players.map { Participant(it.location, it, 0) }, pillarDist, itemTime, totalRounds)
            Arenas.add(arena)
            return arena
        }
    }

    var round = 1
        get() = field
        private set(value) { field = value }

    val world = VoidChunkGenerator.newworld("${Arenas.size()}", pillarDist)

    fun checkSurvivors() {
        val alive = participants.filter { it.player.gameMode == GameMode.SURVIVAL }
        if (alive.size == 1) {
            //TODO: win round messages and sounds here
        }
    }

}