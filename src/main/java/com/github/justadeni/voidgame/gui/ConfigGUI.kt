package com.github.justadeni.voidgame.gui

import com.github.justadeni.countries.Countries
import com.github.justadeni.countries.config.Config
import com.github.justadeni.countries.utils.Logger
import com.github.justadeni.countries.utils.TextUtils.asAction
import com.github.justadeni.countries.utils.TextUtils.replace
import me.xflyiwnl.colorfulgui.`object`.Gui
import me.xflyiwnl.colorfulgui.`object`.GuiItem
import me.xflyiwnl.colorfulgui.`object`.PaginatedGui
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.PlayerInventory
import org.bukkit.scheduler.BukkitRunnable


class ConfigGUI(player: Player, val path: String): ColorfulProvider<PaginatedGui>(player) {

    var replace = hashMapOf<String, String>()
    var additional = emptyMap<String, GuiItem>()

    constructor(player: Player, path: String, vararg replace: Pair<String, String>): this(player, path) {
        this.replace = hashMapOf(*replace)
    }

    constructor(player: Player, path: String, additional: Map<String, GuiItem>): this(player, path) {
        this.additional = additional
    }
    /*
    constructor(player: Player, path: String, additional: Map<String, GuiItem>, replace: Pair<String, String>): this(player, path) {
        this.additional = additional
        this.replace = replace
    }
    */

    constructor(player: Player, path: String, additional: List<GuiItem>, vararg replace: Pair<String, String>): this(player, path) {
        this.replace = hashMapOf(*replace)
        this.additional = additional.withIndex().associate { item: IndexedValue<GuiItem> -> Pair(item.index.toString(), item.value) }
    }

    override fun init() {
        for (key in Config.configurationSection("$path.items")) {
            if (additional.containsKey(key))
                continue

            gui.addMask(key, Countries.gui.staticItem()
                .material(Config.material("$path.items.$key.material"))
                .name(Config.string("$path.items.$key.name")
                    .run { replace(replace) }
                    .take(32))
                .lore(Config.list("$path.items.$key.lore")
                    .run { replace(replace) }
                )
                .action {
                    when(val command = Config.string("$path.items.$key.command")) {
                        "next" -> gui.next()
                        "previous" -> gui.previous()
                        else -> {
                            command
                            .run { replace(replace) }
                            .asAction(it.whoClicked as Player)
                            .invoke()
                        }
                    }
                }
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build()
            )
        }
        for (key in additional.keys) {
            if (key.toIntOrNull() == null)
                gui.addMask(key, additional[key])
            else
                gui.addItem(additional[key])
        }
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

    fun open() {

        object : BukkitRunnable() {
            override fun run() {
                Countries.gui.paginated()
                    .title(Config.string("$path.title")
                        .run { replace(replace) }
                    )
                    .rows(Config.list("$path.mask").size)
                    .mask(Config.list("$path.mask"))
                    .holder(this@ConfigGUI)
                    .build()
            }
        }.runTaskLater(Countries.plugin, 1)
    }

}