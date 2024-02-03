package com.github.justadeni.voidgame.worlds

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.generator.ChunkGenerator

class VoidChunkGenerator private constructor(): ChunkGenerator() {

    /*
    override fun generateSurface(info: WorldInfo, random: Random, x: Int, z: Int, chunkData: ChunkData) {
        for (y in info.minHeight until info.maxHeight) {
            chunkData.setBlock(x, y, z, Material.AIR)
        }
    }

    override fun shouldGenerateNoise(): Boolean {
        return false
    }

    @Deprecated("Deprecated in Java", ReplaceWith("false"))
    override fun shouldGenerateBedrock(): Boolean {
        return false
    }

    override fun shouldGenerateCaves(): Boolean {
        return false
    }
    */

    companion object {

        fun newworld(name: String, pillarDist: Int): World {
            val wc = WorldCreator(name)
            wc.generator(VoidChunkGenerator())
            wc.generateStructures(false)
            wc.type(WorldType.FLAT)
            return wc.createWorld()!!
        }

    }

}