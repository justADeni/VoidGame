package com.github.justadeni.voidgame.worlds

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

    var world: World = null!!
        get() {
            if (field != null)
                return field

            val wc = WorldCreator(name)
            wc.generator(object : ChunkGenerator() {})
            wc.generateStructures(false)
            wc.type(WorldType.FLAT)
            field = wc.createWorld()!!
            return field
        }
        private set(value) {}

    var vertices: List<Location> = emptyList()
        get() {
            if (field.isNotEmpty())
                return field

            val relangle = Math.toRadians(360.0 / playerAmount)
            val vertices = mutableListOf<Location>()
            for (i in 0 until playerAmount) {
                val vertice = Location(world, pillarDist * cos(i * relangle), pillarHeight.toDouble(), pillarDist * sin(i * relangle))
                vertice.block.type = material
                vertices.add(vertice)
            }
            field = vertices
            return field
        }
        private set(value) {}

}