package com.github.justadeni.voidgame.config

import com.github.justadeni.voidgame.VoidGame
import com.github.justadeni.voidgame.misc.FilteredItems
import org.bukkit.Material
import org.bukkit.entity.Player

object Config {

    private val config by lazy { VoidGame.plugin.config }

    private val cache = mutableMapOf<String, Any>()

    fun reload() {
        VoidGame.plugin.reloadConfig()
        FilteredItems.reload()
    }

    private inline fun <reified T : Any> get(key: String): T {
        var value = cache[key]
        if (value != null)
            return value as T

        value = config.get(key) as T
        cache[key] = value
        return value
    }

    fun string(key: String) = get<String>(key)

    fun int(key: String) = get<Int>(key)

    fun bool(key: String) = get<Boolean>(key)

    fun material(key: String) = Material.valueOf(string(key))

    fun list(key: String) = get<List<String>>(key)

    fun sound(key: String): Sound {
        val string = string(key).split(",", limit = 3)
        return Sound(org.bukkit.Sound.valueOf(string[0]), string[1].toDouble().toFloat(), string[2].toDouble().toFloat())
    }

    data class Sound(val sound: org.bukkit.Sound, val volume: Float, val pitch: Float) {
        fun playTo(vararg player: Player) = player.forEach { it.playSound(it, sound, volume, pitch) }
    }

}