package com.github.justadeni.voidgame.gui

import com.github.justadeni.voidgame.VoidGame
import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.misc.CappedInteger
import com.github.justadeni.voidgame.misc.TextUtils.replace
import me.xflyiwnl.colorfulgui.`object`.Gui
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.PlayerInventory
import org.bukkit.scheduler.BukkitRunnable

class AdjustGUI(val player: Player, val arenaBuilder: Arena.ArenaBuilder): ColorfulProvider<Gui>(player) {

    val pillarheight = CappedInteger(
        Config.int("config.defaults.height.default"),
        Config.int("config.defaults.height.min"),
        Config.int("config.defaults.height.max")
    )

    val pillarDistance = CappedInteger(
        Config.int("config.defaults.distance-between-pillars.medium"),
        Config.int("config.defaults.distance-between-pillars.min"),
        Config.int("config.defaults.distance-between-pillars.max")
    )

    val itemTime = CappedInteger(
        Config.int("config.defaults.itemtime.default"),
        Config.int("config.defaults.itemtime.min"),
        Config.int("config.defaults.itemtime.max")
    )

    val totalRounds = CappedInteger(
        Config.int("config.defaults.total-rounds.default"),
        Config.int("config.defaults.total-rounds.min"),
        Config.int("config.defaults.total-rounds.max")
    )

    var material = Config.material("config.defaults.material.default")

    override fun init() {

        gui.addMask("S", VoidGame.gui.staticItem()
            .material(Material.STRUCTURE_VOID)
            .name("&2Start the game")
            .action { arenaBuilder.build() }
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory !is PlayerInventory) {
            event.isCancelled = true
            return
        }
        if (event.isShiftClick && event.clickedInventory is PlayerInventory){
            event.isCancelled = true
            return
        }
    }

    override fun onDrag(event: InventoryDragEvent) {
        for (slot in event.rawSlots){
            if (slot < this.inventory.size) {
                event.isCancelled = true
                break
            }
        }
    }

    fun open() = VoidGame.gui.gui()
            .title("Configure Arena")
            .rows(6)
            .mask(listOf(
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO"
            ))
            .holder(this)
            .build()

}