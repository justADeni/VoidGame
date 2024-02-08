package com.github.justadeni.voidgame.config

import com.github.justadeni.voidgame.VoidGame
import com.sun.jdi.DoubleValue
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

object Config {

    private val config by lazy { VoidGame.plugin.config }

    fun reload() = VoidGame.plugin.reloadConfig()

    fun string(key: String) = config.getString(key)!!

    fun int(key: String) = config.getInt(key)

    fun bool(key: String) = config.getBoolean(key)

    fun material(key: String) = Material.valueOf(config.getString(key) ?: "STONE")

    fun sound(key: String): Sond {
        val string = string(key).split(",", limit = 3)
        return Sond(Sound.valueOf(string[0]), string[1].toDouble().toFloat(), string[2].toDouble().toFloat())
    }

    data class Sond(val sound: Sound, val volume: Float, val pitch: Float) {
        fun playTo(vararg player: Player) = player.forEach { it.playSound(it, sound, volume, pitch) }
    }

}