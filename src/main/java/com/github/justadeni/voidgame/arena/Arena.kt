package com.github.justadeni.voidgame.arena

import com.github.justadeni.voidgame.misc.GeneralUtils.give
import com.github.justadeni.voidgame.misc.FilteredItems
import com.github.justadeni.voidgame.misc.RepeatingTask
import com.github.justadeni.voidgame.worlds.ArenaWorld
import com.zorbeytorunoglu.kLib.extensions.clearAllInventory
import com.zorbeytorunoglu.kLib.extensions.heal
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player


class Arena private constructor(val participants: List<Participant>, pillarDist: Int, pillarHeight: Int, pillarMaterial: Material, itemTime: Int, private val totalRounds: Int) {

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

    //private val itemGiver = ItemGiver()

    private val itemgivingTask = RepeatingTask({
            participants
                .filter { it.player.gameMode == GameMode.SURVIVAL }
                .forEach {
                    if (it.trollmode) it.player.give(FilteredItems.troll()) else it.player.give(FilteredItems.random())
            }
        },
        itemTime*1000L
    )

    var round = 0
        get() = field
        private set(value) { field = value }

    val arenaworld = ArenaWorld(
        name = "vg${Arenas.size()}",
        playerAmount = participants.size,
        pillarDist = pillarDist,
        pillarHeight = pillarHeight,
        material = pillarMaterial
    )

    val placedBlocks = mutableListOf<Location>()

    init {
        startRound()
    }

    private fun startRound() {
        round++
        placedBlocks.forEach { it.block.type = Material.AIR }
        placedBlocks.clear()

        participants.forEachIndexed { index, participant ->
            participant.player.clearAllInventory()
            participant.player.gameMode = GameMode.SURVIVAL
            participant.player.activePotionEffects.clear()
            participant.player.heal()
            participant.player.teleport(arenaworld.vertices[index])
        }
        itemgivingTask.start()
    }

    fun participant(player: Player): Participant? {
        return participants.firstOrNull { it.player == player }
    }

    fun checkSurvivors() {
        val alive = participants.filter { it.player.gameMode == GameMode.SURVIVAL }
        if (alive.size == 1) {
            //TODO: win round messages and sounds here
        }
    }

}