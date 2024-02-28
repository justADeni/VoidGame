package com.github.justadeni.voidgame.arena

import com.github.justadeni.voidgame.config.Config
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
import org.bukkit.inventory.PlayerInventory


class Arena private constructor(val participants: List<Participant>, pillarDist: Int, pillarHeight: Int, pillarMaterial: Material, itemTime: Int, private val totalRounds: Int) {

    data class Participant(val beforePos: Location, val beforeInventory: PlayerInventory, var player: Player, var won: Int, var trollmode: Boolean = false)

    class ArenaBuilder() {
        val players = mutableListOf<Player>()

        var pillarDist = Config.int("config.defaults.distance-between-pillars.medium")

        var pillarHeight = Config.int("config.defaults.height.default")

        var pillarMaterial = Config.material("config.defaults.material.default")

        var itemTime = Config.int("config.defaults.itemtime.default")

        var totalRounds = Config.int("config.defaults.total-rounds.default")

        fun build(): Arena {
            val arena = Arena(players.map { Participant(it.location, it.inventory, it, 0) }, pillarDist, pillarHeight, pillarMaterial, itemTime, totalRounds)
            Arenas.add(arena)
            arena.startRound()
            return arena
        }
    }

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
        "vg${Arenas.size()}",
        participants.size,
        pillarDist,
        pillarHeight,
        pillarMaterial
    )

    val placedBlocks = mutableListOf<Location>()

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

    enum class Event {
        LEAVE,
        REJOIN,
        DIE,
        WIN
    }

    fun announce(event: Event, player: Player): Unit = when(event) {
        Event.LEAVE -> TODO()
        Event.REJOIN -> TODO()
        Event.DIE -> TODO()
        Event.WIN -> TODO()
    }

    private fun checkSurvivors() {
        val (alive, dead) = participants.partition { it.player.gameMode == GameMode.SURVIVAL }
        if (alive.size == 1) {

        }
    }

    fun end() {
        itemgivingTask.stop()
        Arenas.remove(this)
        participants.forEach { if (it.player.isOnline) it.player.teleport(it.beforePos) }
    }

}