package com.github.justadeni.voidgame.misc

import com.github.justadeni.voidgame.config.Config
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object FilteredItems {

    private lateinit var entries: List<Material>// = Material.entries.filterNot { entry -> Config.list("drop-item.filter").any { entry.toString().contains(it) } }

    fun reload() {
        entries = Material.entries.filterNot { entry -> Config.list("drop-item.filter").any { entry.toString().contains(it) } }
    }

    fun random() = ItemStack(entries.random())

    fun troll() = ItemStack(Config.material("troll.material"))

}