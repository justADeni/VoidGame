package com.github.justadeni.voidgame.worlds

import com.github.justadeni.voidgame.config.Config
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.generator.ChunkGenerator
import kotlin.math.cos
import kotlin.math.sin

class ArenaWorld(
    val name: String,
    val playerAmount: Int,
    val pillarDist: Int,
    val pillarHeight: Int,
    val material: Material,
) {

    val respawnloc: Location = run {
        Location(
            world,
            0.0,
            Config.int("config.defaults.start-y") + pillarHeight + 2.0,
            0.0
        )
    }

    val world: World = run {
        val wc = WorldCreator(name)
        wc.generator(object : ChunkGenerator() {})
        wc.generateStructures(false)
        wc.type(WorldType.FLAT)
        wc.createWorld()!!
    }

    var vertices: List<Location> = run {
        val relangle = Math.toRadians(360.0 / playerAmount)
        val vertices = mutableListOf<Location>()
        for (i in 0 until playerAmount) {
            val x = pillarDist * cos(i * relangle)
            val z = pillarDist * sin(i * relangle)
            val starty = Config.int("config.defaults.start-y")
            for (y in starty..pillarHeight) {
                Location(world, x, y.toDouble(), z).block.type = material
            }
            vertices.add(Location(world, x, starty + pillarHeight.toDouble(), z))
        }
        vertices
    }


}