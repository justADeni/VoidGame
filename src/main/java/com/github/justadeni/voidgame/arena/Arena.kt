package com.github.justadeni.voidgame.arena

import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.misc.FilteredItems
import com.github.justadeni.voidgame.misc.GeneralUtils.give
import com.github.justadeni.voidgame.misc.GeneralUtils.sendTo
import com.github.justadeni.voidgame.misc.RepeatingTask
import com.github.justadeni.voidgame.worlds.ArenaWorld
import com.zorbeytorunoglu.kLib.extensions.clearAllInventory
import com.zorbeytorunoglu.kLib.extensions.heal
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
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

        fun cancel() {
            ArenaBuilders.remove(this)
            this.players.clear()
            this.players.forEach { "Queue was cancelled by leader.".sendTo(it) }
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

    fun announce(event: Event, player: Player) {
        when(event) {
            Event.LEAVE -> {
                this.participants.forEach { "${player.name} left the game.".sendTo(it.player) }
            }

            Event.REJOIN -> {
                this.participants.forEach { "${player.name} rejoined the game.".sendTo(it.player) }
            }

            Event.DIE -> {
                if (round < totalRounds)
                    "You died. You will respawn in the next round.".sendTo(player)
                else
                    "You died.".sendTo(player)

                checkSurvivors()
            }

            Event.WIN -> {
                if (round < totalRounds)
                    this.participants.forEach { "${player.name} won this round.".sendTo(it.player) }
                else
                    this.participants.forEach { "${player.name} won the game!".sendTo(it.player) }

                val fw = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
                val meta = fw.fireworkMeta
                meta.addEffects(
                    FireworkEffect.builder()
                    .withColor(Color.AQUA)
                    .withFade(Color.LIME)
                    .withTrail()
                    .withFlicker()
                    .build()
                )
                fw.fireworkMeta = meta

                Scopes.supervisorScope.launch {
                    delay(3000)
                    if (round == totalRounds)
                        end()
                    else
                        startRound()
                }
            }
        }
    }

    private fun checkSurvivors() {
        val (alive, _) = participants.partition { it.player.gameMode == GameMode.SURVIVAL }
        if (alive.size == 1) {
            announce(Event.WIN, alive[0].player)
        }
    }

    fun end() {
        itemgivingTask.stop()
        Arenas.remove(this)
        participants.forEach { if (it.player.isOnline) it.player.teleport(it.beforePos) }
    }

}