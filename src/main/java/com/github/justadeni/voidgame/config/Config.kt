package com.github.justadeni.voidgame.config

import com.github.justadeni.voidgame.VoidGame
import com.github.justadeni.voidgame.misc.Logger
import org.apache.commons.io.FileUtils
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.awt.Color
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.CodeSource
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipInputStream

import com.github.justadeni.voidgame.misc.TextUtils.colorize
import org.bukkit.plugin.java.JavaPlugin

object Config {

    private val datafolder = VoidGame.plugin.dataFolder

    private val cache = ConcurrentHashMap<String, Any>(1000)
    private val configs = hashMapOf<String, FileConfiguration>()

    fun loadConfigs() {
        val src: CodeSource = VoidGame::class.java.getProtectionDomain().codeSource
        val zip = ZipInputStream(src.location.openStream())

        while(true) {
            val e = zip.getNextEntry() ?: break
            if (!e.name.contains(".yml") || e.name.contains("plugin.yml"))
                continue

            val file = File(datafolder.path + "/" + e.name)
            if (!file.exists()){
                val url = VoidGame::class.java.getResource("/${e.name}") ?: continue
                FileUtils.copyURLToFile(url, file)
            }
            configs[e.name.replace(".yml", "")] = YamlConfiguration.loadConfiguration(file)
        }
        zip.close()
    }

    fun reload() {
        for (config in configs.keys){
            val file = File(datafolder, "$config.yml")
            if (file.exists())
                configs[config] = YamlConfiguration.loadConfiguration(file)
        }
        cache.clear()
    }

    val NOT_FOUND: String by lazy { string("config.messages.not-found") }

    val PREFIX: String by lazy { string("config.messages.prefix").colorize()!! }

    // null == try again, exception == failed
    @Throws(ConfigurationException::class)
    private inline fun <reified T> get(path: String): T? {
        val value = cache[path]
        if (value != null) {
            try {
                return value as T
            } catch (e: Exception) {
                cache.remove(path)
                /*
                Logger.warn(string("messages.console.error-type")
                    .replace("%value%", value.toString())
                    .replace("%type%", T::class.java.simpleName)
                    .replace("%file%", path.split(".")[0] + ".yml"))
                */
            }
        }
        val split = path.split(".", limit = 2)
        val filename = "${split[0]}.yml"

        var fileconfiguration = configs[split[0]]

        if (fileconfiguration == null) {
            val file = File(datafolder.path + "/" + filename)
            if (file.exists()) {
                //Logger.warn(string("messages.console.restoring-file").replace("%file%", filename))
            } else {
                val url: URL? = VoidGame::class.java.getResource("/${filename}")
                if (url == null) {
                    //Logger.warn(string("messages.console.error-file").replace("%file%", filename))
                    throw ConfigurationException()
                }
                //Logger.warn(string("messages.console.restoring-file").replace("%file%", filename))
                FileUtils.copyURLToFile(url, file)
            }
            fileconfiguration = try {
                YamlConfiguration.loadConfiguration(file)
            } catch (e: IllegalStateException) {
                //Logger.warn(string("messages.console.error-file").replace("%file%", filename))
                throw ConfigurationException()
            }
            configs[split[0]] = fileconfiguration!!
        }

        val found = fileconfiguration.get(split[1])
        if (found == null) {
            val url: URL? = VoidGame::class.java.getResource("/${filename}")
            if (url == null) {
                //Logger.warn(string("messages.console.error-key").replace("%file%", filename).replace("%key%", split[1]))
                throw ConfigurationException()
            }
            val text = try {
                val stream = url.openStream()
                val bytes = stream.readAllBytes()
                stream.close()
                String(bytes, StandardCharsets.UTF_8)
            } catch (e: IOException) {
                //Logger.warn(string("messages.console.error-key").replace("%file%", filename).replace("%key%", split[1]))
                throw ConfigurationException()
            }
            val tempfile = try {
                File(datafolder.path + "/tempfile.yml")
            } catch (e: NullPointerException) {
                //Logger.warn(string("messages.console.error-key").replace("%file%", filename).replace("%key%", split[1]))
                throw ConfigurationException()
            }
            tempfile.writeText(text)
            val tempfileconfiguration = try {
                YamlConfiguration.loadConfiguration(tempfile)
            } catch (e: IllegalStateException) {
                //Logger.warn(string("messages.console.error-key").replace("%file%", filename).replace("%key%", split[1]))
                throw ConfigurationException()
            } finally {
                tempfile.delete()
            }
            tempfile.delete()
            val tempvalue = tempfileconfiguration.get(split[1])
            if (tempvalue is T) {
                fileconfiguration.set(split[1], tempvalue)
                fileconfiguration.save(File(datafolder, filename))
                //Logger.warn(string("messages.console.restoring-key").replace("%file%", filename).replace("%key%", split[1]))
                cache[path] = tempvalue as Any
                return tempvalue
            } else {
                //Logger.warn(string("messages.console.error-key").replace("%file%", filename).replace("%key%", split[1]))
                throw ConfigurationException()
            }
        }
        if (found is T) {
            cache[path] = found
            return found
        } else {
            //Logger.warn(string("messages.console.error-key").replace("%file%", filename).replace("%key%", split[1]))
            throw ConfigurationException()
        }
    }

    fun string(path: String): String {
        return try {
            get<String>(path) ?: string(path)
        } catch (e: ConfigurationException) {
            "Error"
        }
    }

    fun int(path: String): Int {
        return try {
            get<Int>(path) ?: int(path)
        } catch (e: ConfigurationException) {
            42
        }
    }

    fun list(path: String): List<String> {
        return try {
            get<List<String>>(path) ?: list(path)
        } catch (e: ConfigurationException) {
            emptyList()
        }
    }

    fun bool(path: String): Boolean {
        return try {
            get<Boolean>(path) ?: bool(path)
        } catch (e: ConfigurationException) {
            false
        }
    }

    /*
    private val colorregex = Regex("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\s*,\\s*){2}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$")

    fun color(path: String): Color {
        val string = string(path)
        if (string.matches(colorregex)) {
            val split = string.split(",")
            return Color(split[0].toInt(), split[1].toInt(), split[2].toInt())
        } else {
            return Color(0,0,0)
        }
    }
    */

    fun material(path: String): Material {
        return try {
            Material.valueOf(string(path))
        } catch (e: EnumConstantNotPresentException) {
            Material.STONE
        }
    }

    fun configurationSection(path: String): List<String> {
        return try {
            get<ConfigurationSection>(path)?.getKeys(false)?.toList() ?: configurationSection(path)
        } catch (e: ConfigurationException) {
            emptyList()
        }
    }

}