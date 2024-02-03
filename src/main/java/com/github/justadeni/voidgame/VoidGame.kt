package com.github.justadeni.voidgame

import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.command.Command
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class VoidGame : JavaPlugin() {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        plugin = this
        saveDefaultConfig()
        getCommand("voidgame")!!.setExecutor(Command())

    }

    override fun onDisable() {
        for (arena in Arenas.get()) {
            for (participant in arena.participants) {
                if (!participant.player.isOnline)
                    continue

                participant.player.teleport(participant.beforePos)
            }
            Bukkit.unloadWorld(arena.world, false)
            val file = File("${Bukkit.getWorldContainer().path}/${arena.world.name}")
            file.deleteRecursively()
        }
    }
}
