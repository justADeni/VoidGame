package com.github.justadeni.voidgame.misc

import com.github.justadeni.voidgame.config.Config
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

import me.xflyiwnl.colorfulgui.util.ColorUtils

object TextUtils {

    /**
     * Only use with strings that already
     * have placeholders replaced by values!
     */
    fun String.toComponent(): TextComponent {
        if (!this.contains(">(")){
            return TextComponent(ColorUtils.colorize(this))
        }
        val collective = mutableListOf<TextComponent>()
        for (part in this.splitAt("<", 0)) {
            if (!part.contains(">(")) {
                collective.add(TextComponent(part.colorize()))
                continue
            }

            val components = mutableListOf<TextComponent>()
            val square = part.substringAfter('<').substringBefore('>')
            val round = part.substringAfter('(').substringBefore(')')
            for (string in part.split("<$square>($round)")) {
                components.add(TextComponent(ColorUtils.colorize(string)))
            }

            val component = TextComponent(ColorUtils.colorize(square.remove('<', '>')))

            val action = round.remove('(', ')')

            if (action.split(",", limit = 2).size > 1){
                component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, listOf(TextComponent(ColorUtils.colorize(action.substringAfterLast(",")))).toTypedArray())
            } else {
                component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, listOf(TextComponent(ColorUtils.colorize(Config.string("country-config.default-hover-text")))).toTypedArray())
            }
            val primary = action.substringBeforeLast(",").substring(1)

            when (action[0]) {
                '/' -> component.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$primary")
                '-' -> component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$primary")
                '*' -> component.clickEvent = ClickEvent(ClickEvent.Action.CHANGE_PAGE, primary)
                '~' -> component.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, primary)
            }

            components.add(1, component)
            collective.addAll(components)
        }
        val final = collective[0]

        if (collective.size > 1) {
            for (i in 1..< collective.size) {
                final.addExtra(collective[i])
            }
        }
        return final
    }

    fun List<String>.toComponent(): TextComponent {
        val textComponent = TextComponent("")
        if (this.isEmpty())
            return textComponent

        for (string in this) {
            textComponent.addExtra((string + "\n").toComponent())
        }
        return textComponent
    }

    fun List<String>.numerate(): List<String> {
        val newlist = mutableListOf<String>()
        var num = 1
        for (string in this) {
            var replaced = string
            var i = 0
            while (i <= string.length - 3) {
                if (replaced.substring(i, i + 3) == "[n]") {
                    replaced = replaced.substring(0,i) + "[$num]" + replaced.substring(i + 3, replaced.length)
                    num++
                    i += 3
                } else {
                    i++
                }
            }
            newlist.add(replaced)
        }
        return newlist
    }

    private fun String.substring(a: Char, b: Char): String {
        return this.substring(this.indexOf(a), this.indexOf(b))
    }

    private fun String.remove(vararg c: Char): String {
        var string = this
        for (i in c) {
            string = string.replace(i.toString(), "")
        }
        return string
    }

    fun String.replace(map: Map<String, String>): String {
        if (map.isEmpty())
            return this

        var string = this
        for (key in map.keys) {
            string = string.replace(key, map[key]!!)
        }
        return string
    }

    fun List<String>.replace(map: Map<String, String>): List<String> {
        if (map.isEmpty())
            return this

        return this.map { it.replace(map) }
    }

    fun List<String>.replace(occurence: String, replacement: String): List<String> {
        return this.map { it.replace(occurence, replacement) }
    }

    fun List<String>.replacefirst(occurence: String, replacement: String): List<String> {
        return this.map { it.replaceFirst(occurence, replacement) }
    }

    fun List<String>.replace(occurence: String, replacement: String, fromIndex: Int): List<String> {
        return this.mapIndexed { i, it ->
            if (i >= fromIndex)
                it.replace(occurence, replacement)
            else
                it
        }
    }


    private fun String.splitAt(split: String, bywhich: Int): List<String> {
        if (this.length <= split.length)
            return listOf(this)

        val cutindices = mutableListOf<Int>()
        var found = 0
        for (i in 0 .. (this.length - split.length)) {
            val window = this.substring(i, i + split.length)
            if (window == split) {
                found++
                if (found >= bywhich)
                    cutindices.add(i)
            }
        }
        cutindices.add(this.length)

        if (cutindices.size == 1)
            return listOf(this)

        val cut = mutableListOf<String>()
        var previous = 0
        for (indice in cutindices) {
            if (indice == 0)
                continue

            cut.add(this.substring(previous, indice))
            previous = indice
        }
        return cut
    }

    fun String.colorize() = ColorUtils.colorize(this)

    fun List<String>.colorize() = this.map { it.colorize() }

    fun String.contains(list: List<String>): Boolean {
        for (item in list)
            if (this.contains(item, ignoreCase = true))
                return true
        return false
    }

    fun String.asAction(player: Player): () -> Unit {

        if (Config.bool("country-config.debug-mode"))
            Logger.log("${player.name}: $this")

        if (this == "Error" || this.isBlank())
            return {}
        return {
            for (split in this.split("|"))
                player.performCommand(split.replace("/", ""))
        }
    }
}