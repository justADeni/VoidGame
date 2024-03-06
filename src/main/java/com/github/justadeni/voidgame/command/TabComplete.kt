package com.github.justadeni.voidgame.command

import com.github.justadeni.voidgame.misc.Rank
import com.github.justadeni.voidgame.misc.Status
import com.github.justadeni.voidgame.misc.StatusAndRank
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TabComplete: TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (sender !is Player)
            return emptyList()

        val list = mutableListOf<String>()
        val (status, rank) = StatusAndRank.get(sender)

        //println("args: ${args.joinToString()}, size: ${args.size}")
        if (args.isEmpty() || !arrayOf("create", "accept", "invite", "cancel", "leave", "troll").contains(args[0])) {
            when (status) {
                Status.FREE -> list.add("create")
                Status.INVITED -> list.add("accept")
                Status.IN_LOBBY -> if (rank == Rank.LEADER) list.addAll(arrayOf("invite", "cancel")) else list.add("leave")
                Status.IN_GAME -> if (rank == Rank.LEADER) list.add("troll")
            }
        } else if (rank == Rank.LEADER)  {
            if (args[0].lowercase() == "invite" || args[0].lowercase() == "troll") {
                list.addAll(StatusAndRank.groupedPlayers(sender)
                    .filterNot { it == sender }
                    .map { it.name })
            }
        }
        return list
    }

}