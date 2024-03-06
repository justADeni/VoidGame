package com.github.justadeni.voidgame

import com.github.justadeni.voidgame.arena.Arenas
import com.github.justadeni.voidgame.command.Command
import com.github.justadeni.voidgame.command.TabComplete
import com.github.justadeni.voidgame.listeners.*
import com.github.justadeni.voidgame.misc.FilteredItems
import com.zorbeytorunoglu.kLib.MCPlugin
import com.zorbeytorunoglu.kLib.task.repeatAsync
import com.zorbeytorunoglu.kLib.task.suspendFunction
import me.xflyiwnl.colorfulgui.ColorfulGUI
import me.xflyiwnl.colorfulgui.`object`.Gui
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread

class VoidGame : JavaPlugin() {

    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var gui: ColorfulGUI
    }

    override fun onEnable() {
        plugin = this
        gui = ColorfulGUI(this)
        saveDefaultConfig()
        getCommand("voidgame")!!.setExecutor(Command())
        getCommand("voidgame")!!.setTabCompleter(TabComplete())
        server.pluginManager.registerEvents(PlayerJoin(),this)
        server.pluginManager.registerEvents(PlayerMove(),this)
        server.pluginManager.registerEvents(PlayerDeath(),this)
        server.pluginManager.registerEvents(PlayerRespawn(), this)
        server.pluginManager.registerEvents(PlayerLeave(), this)
        server.pluginManager.registerEvents(BlockPlace(), this)
        FilteredItems.reload()
    }

    override fun onDisable() {
        for (arena in Arenas.get()) {
            arena.end()
            Bukkit.unloadWorld(arena.arenaworld.world, false)
            arena.arenaworld.world.worldFolder.deleteRecursively()
        }
    }
}
