package com.github.justadeni.voidgame.gui

import com.github.justadeni.voidgame.VoidGame
import com.github.justadeni.voidgame.arena.Arena
import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.misc.CappedInteger
import com.github.justadeni.voidgame.misc.GeneralUtils.sendTo
import com.github.justadeni.voidgame.misc.TextUtils.replace
import me.xflyiwnl.colorfulgui.`object`.Gui
import me.xflyiwnl.colorfulgui.`object`.action.GuiAction
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.PlayerInventory
import org.bukkit.scheduler.BukkitRunnable

class AdjustGUI(val player: Player, val arenaBuilder: Arena.ArenaBuilder): ColorfulProvider<Gui>(player, 1) {

    var material = Config.material("config.defaults.material.default")

    //masks in this order: Description, Decrement, Increment
    private fun addButtons(name: String, materials: Array<Material>, masks: String, cappedInteger: CappedInteger) {
        gui.addMask(name[0].toString(), VoidGame.gui.staticItem()
            .material(materials[0])
            .name(name)
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )
        if (cappedInteger.final)
            return

        gui.addMask(name[1].toString(), VoidGame.gui.staticItem()
            .material(materials[1])
            .name("&e-1")
            .action {
                if (cappedInteger - 1 == CappedInteger.Result.DENIED)
                    "&cYou've reached the limit.".sendTo(player)
            }
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )
        gui.addMask(name[2].toString(), VoidGame.gui.staticItem()
            .material(materials[2])
            .name("&e+1")
            .action {
                if (cappedInteger + 1 == CappedInteger.Result.DENIED)
                    "&cYou've reached the limit.".sendTo(player)
            }
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )
    }

    override fun init() {

        gui.addMask("S", VoidGame.gui.staticItem()
            .material(Material.STRUCTURE_VOID)
            .name("&2Start the game")
            .action {
                arenaBuilder.build()
            }
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )

        addButtons(
            name = "&aPillar Height",
            materials = arrayOf(Material.COBBLED_DEEPSLATE, Material.STONE_BUTTON, Material.STONE_BUTTON),
            masks = "QWE",
            CappedInteger(
                Config.int("config.defaults.height.default"),
                Config.int("config.defaults.height.min"),
                Config.int("config.defaults.height.max")
            )
        )

        addButtons(
            name = "&bPillar Distance",
            materials = arrayOf(Material.DARK_PRISMARINE, Material.PRISMARINE_CRYSTALS, Material.PRISMARINE_CRYSTALS),
            masks = "RTY",
            CappedInteger(
                Config.int("config.defaults.distance-between-pillars.default"),
                Config.int("config.defaults.distance-between-pillars.min"),
                Config.int("config.defaults.distance-between-pillars.max")
            )
        )

        addButtons(
            name = "&bTime between items",
            materials = arrayOf(Material.CLOCK, Material.GOLD_NUGGET, Material.GOLD_NUGGET),
            masks = "UIP",
            CappedInteger(
                Config.int("config.defaults.itemtime.default"),
                Config.int("config.defaults.itemtime.min"),
                Config.int("config.defaults.itemtime.max")
            )
        )

        addButtons(
            name = "&bTotal rounds",
            materials = arrayOf(Material.END_STONE, Material.ENDER_EYE, Material.ENDER_EYE),
            masks = "ASD",
            CappedInteger(
                Config.int("config.defaults.total-rounds.default"),
                Config.int("config.defaults.total-rounds.min"),
                Config.int("config.defaults.total-rounds.max")
            )
        )

        gui.addMask("M", VoidGame.gui.dynamicItem()
            .material(Material.STRUCTURE_VOID)
            .name("&ePick Pillar Material")
            .action {
                ChooseMaterialGUI(player, this).open()
            }
            .update {
                val meta = it.item.itemMeta
                it.item.type = Material.entries.random()
                it.item.itemMeta = meta
            }
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
                "QOOOOOOOA",
                "WOOOSOOOS",
                "EOOOOOOOD",
                "OOROOOUOO",
                "OOTOMOIOO",
                "OOYOOOPOO"
            ))
            .holder(this)
            .build()

}