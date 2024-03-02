package com.github.justadeni.voidgame.misc

import com.github.justadeni.voidgame.VoidGame
import com.github.justadeni.voidgame.config.Config
import com.github.justadeni.voidgame.misc.TextUtils.toComponent
import com.zorbeytorunoglu.kLib.extensions.Inventory
import com.zorbeytorunoglu.kLib.extensions.dropItemNaturally
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

object GeneralUtils {

    fun String.sendTo(sender: CommandSender) {
        if (!this.contains("<"))
            sender.sendMessage(this)
        else
            sender.spigot().sendMessage(this.toComponent())
    }

    fun Player.give(item: ItemStack) {
        val leftovers = this.inventory.addItem(item).values

        val destacked = mutableListOf<ItemStack>()
        for (stack in leftovers) {
            val over64 = stack.amount/64
            if (over64 > 0) {
                for (i in 0..over64)
                    destacked.add(ItemStack(stack.type, 64))
            }
            val left = stack.amount % 64
            if (left > 0)
                destacked.add(ItemStack(stack.type, left))
        }

        object : BukkitRunnable() {
            override fun run() {
                destacked.forEach { this@give.player?.location?.dropItemNaturally(it) }
            }
        }.runTask(VoidGame.plugin)
    }

    fun Player.forceClose() {
        player!!.openInventory(Inventory(9, null))
        player!!.closeInventory()
    }

    fun ItemStack?.glow() {
        val meta = this?.itemMeta ?: return
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.addEnchant(Enchantment.LURE, 1, true)
        this.itemMeta = meta
    }

    fun ItemStack?.unglow() {
        val meta = this?.itemMeta ?: return
        meta.removeEnchant(Enchantment.LURE)
        this.itemMeta = meta
    }

}