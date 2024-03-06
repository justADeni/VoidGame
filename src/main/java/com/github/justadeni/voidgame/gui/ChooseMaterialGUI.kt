package com.github.justadeni.voidgame.gui

import com.github.justadeni.voidgame.VoidGame
import com.github.justadeni.voidgame.config.Config
import me.xflyiwnl.colorfulgui.`object`.Gui
import me.xflyiwnl.colorfulgui.`object`.PaginatedGui
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.PlayerInventory

class ChooseMaterialGUI(@get:JvmName("gui_player") val player: Player, val adjustGUI: AdjustGUI): ColorfulProvider<PaginatedGui>(player) {

    override fun init() {
        gui.addMask("B", VoidGame.gui.staticItem()
            .material(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            .name(" ")
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )
        gui.addMask("P", VoidGame.gui.staticItem()
            .material(Material.RED_STAINED_GLASS_PANE)
            .name("&ePrevious page")
            .action { gui.previous() }
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )
        gui.addMask("N", VoidGame.gui.staticItem()
            .material(Material.GREEN_STAINED_GLASS_PANE)
            .name("&eNext page")
            .action { gui.next() }
            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            .build()
        )

        if (!Config.bool("config.defaults.material.editable"))
            return

        Material.entries
            .asSequence()
            .filterNot { material ->
                Config.list("config.defaults.material.filter").any { it.contains(material.toString()) }
            }
            .filter { it.isBlock }
            .forEach { material -> gui.addItem(
                VoidGame.gui.staticItem()
                    .material(material)
                    //.lore(emptyList())
                    .action {
                        adjustGUI.material = material
                        adjustGUI.open()
                    }
                    .build()
        )}
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

    fun open() = VoidGame.gui.paginated()
        .title("Pick pillar block")
        .rows(6)
        .mask(listOf(
            "PBBBBBBBN",
            "OOOOOOOOO",
            "OOOOOOOOO",
            "OOOOOOOOO",
            "OOOOOOOOO",
            "OOOOOOOOO"
        ))
        .holder(this)
        .build()

}