package com.github.justadeni.voidgame.config

import com.github.justadeni.voidgame.VoidGame

object Config {

    private val config by lazy { VoidGame.plugin.config }

    fun reload() = VoidGame.plugin.reloadConfig()

    fun string(key: String) = config.getString(key)!!

    fun int(key: String) = config.getInt(key)

    fun bool(key: String) = config.getBoolean(key)

}