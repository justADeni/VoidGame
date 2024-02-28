package com.github.justadeni.voidgame.command

import com.github.justadeni.voidgame.misc.Rank
import com.github.justadeni.voidgame.misc.Status
import com.github.justadeni.voidgame.misc.StatusAndRank
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TabComplete: TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        if (sender !is Player)
            return mutableListOf()

        val list = mutableListOf<String>()

        val (status, rank) = StatusAndRank.get(sender)

        when (status) {
            Status.FREE -> list.add("create")
            Status.INVITED -> list.add("accept")
            Status.IN_LOBBY -> if (rank == Rank.LEADER) list.add("invite") else list.add("leave")
            Status.IN_GAME -> if (rank == Rank.LEADER) list.add("troll")
        }

        return list
    }

}