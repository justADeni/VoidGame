package com.github.justadeni.voidgame.misc

import com.github.justadeni.voidgame.arena.ArenaBuilders
import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.invitations.Invitations
import org.bukkit.entity.Player

enum class Status {
    FREE,
    INVITED,
    IN_LOBBY,
    IN_GAME,
}

enum class Rank {
    OTHER,
    PARTICIPANT,
    LEADER
}

object StatusAndRank {
    fun get(player: Player): Pair<Status, Rank> {
        val game = Arenas.ofPlayer(player)
        if (game != null) {
            return if (game.participants[0].player == player)
                Pair(Status.IN_GAME, Rank.LEADER)
            else
                Pair(Status.IN_GAME, Rank.PARTICIPANT)
        }

        val lobby = ArenaBuilders.ofPlayer(player)
        if (lobby != null) {
            return if (lobby.players[0] == player)
                Pair(Status.IN_LOBBY, Rank.LEADER)
            else
                Pair(Status.IN_LOBBY, Rank.PARTICIPANT)
        }

        if (Invitations.contains(player))
            return Pair(Status.INVITED, Rank.OTHER)

        return Pair(Status.FREE, Rank.OTHER)
    }

    fun groupedPlayers(player: Player): List<Player> {
        val game = Arenas.ofPlayer(player)
        if (game != null)
            return game.participants.map { it.player }

        val lobby = ArenaBuilders.ofPlayer(player)
        if (lobby != null)
            return lobby.players

        return emptyList()
    }
}
