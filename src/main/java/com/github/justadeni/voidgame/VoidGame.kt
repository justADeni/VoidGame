package com.github.justadeni.voidgame

import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.command.Command
import com.github.justadeni.voidgame.listeners.PlayerDeath
import com.github.justadeni.voidgame.listeners.PlayerMove
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
        server.pluginManager.registerEvents(PlayerMove(),this)
        server.pluginManager.registerEvents(PlayerDeath(),this)
    }

    override fun onDisable() {
        for (arena in Arenas.get()) {
            for (participant in arena.participants) {
                if (!participant.player.isOnline)
                    continue

                participant.player.teleport(participant.beforePos)
            }
            Bukkit.unloadWorld(arena.world, false)
            arena.world.worldFolder.deleteRecursively()
        }
    }
}
