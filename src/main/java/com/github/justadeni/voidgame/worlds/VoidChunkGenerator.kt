package com.github.justadeni.voidgame.worlds

import com.github.justadeni.voidgame.config.Config
import org.bukkit.Location
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

        private fun coords(playerAmount: Int, pillarDist: Int, pillarHeight: Int) {

        }

        private fun placepillar(location: Location, height: Int, material: Material) {
            for (i in 0..< height) {
                location.block.type = material
                location.add(0.0,1.0,0.0)
            }
        }

        fun newworld(name: String, playerAmount: Int, pillarDist: Int, pillarHeight: Int, material: Material): World {
            val wc = WorldCreator(name)
            wc.generator(VoidChunkGenerator())
            wc.generateStructures(false)
            wc.type(WorldType.FLAT)



            return wc.createWorld()!!
        }

    }

}