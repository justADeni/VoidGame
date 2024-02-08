package com.github.justadeni.voidgame.arena

import com.github.justadeni.voidgame.worlds.ArenaWorld
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player


class Arena private constructor(val participants: List<Participant>, pillarDist: Int, pillarHeight: Int, pillarMaterial: Material, private val itemTime: Int, private val totalRounds: Int) {

    data class Participant(val beforePos: Location, val player: Player, var won: Int, var trollmode: Boolean = false)

    class ArenaBuilder() {
        val players = mutableSetOf<Player>()

        var pillarDist = 0

        var pillarHeight = 0

        var pillarMaterial = Material.BEDROCK

        var itemTime = 0

        var totalRounds = 0

        fun build(): Arena {
            val arena = Arena(players.map { Participant(it.location, it, 0) }, pillarDist, pillarHeight, pillarMaterial, itemTime, totalRounds)
            Arenas.add(arena)
            return arena
        }
    }

    var round = 1
        get() = field
        private set(value) { field = value }

    val arenaworld = ArenaWorld(
        name = "vg${Arenas.size()}",
        playerAmount = participants.size,
        pillarDist = pillarDist,
        pillarHeight = pillarHeight,
        material = pillarMaterial
    )

    fun checkSurvivors() {
        val alive = participants.filter { it.player.gameMode == GameMode.SURVIVAL }
        if (alive.size == 1) {
            //TODO: win round messages and sounds here
        }
    }

}