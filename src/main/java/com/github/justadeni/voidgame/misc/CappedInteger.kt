package com.github.justadeni.voidgame.misc

import kotlin.reflect.KMutableProperty0

class CappedInteger(private val property: KMutableProperty0<Int>, private val min: Int, private val max: Int) {

    enum class Result {
        APPROVED,
        DENIED
    }

    init {
        if (min > max || property.get() > max || property.get() < min)
            throw IllegalArgumentException()
    }

    val final : Boolean
        get() = min == max

    operator fun plus(another: Int): Result {
        if (property.get() + another > max)
            return Result.DENIED

        property.set(property.get() + another)
        return Result.APPROVED
    }

    operator fun minus(another: Int): Result {
        if (property.get() - another < min)
            return Result.DENIED

        property.set(property.get() - another)
        return Result.APPROVED
    }

}