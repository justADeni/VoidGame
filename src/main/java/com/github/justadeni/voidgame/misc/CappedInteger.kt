package com.github.justadeni.voidgame.misc

class CappedInteger(default: Int, private val min: Int, private val max: Int) {

    enum class Result {
        APPROVED,
        DENIED
    }

    init {
        if (min > max || default > max || default < min)
            throw IllegalArgumentException()
    }

    var value = default
        get() = field
        private set(newvalue) { field = newvalue }

    val final : Boolean
        get() = min == max

    operator fun plus(another: Int): Result {
        if (value + another > max)
            return Result.DENIED

        value += another
        return Result.APPROVED
    }

    operator fun minus(another: Int): Result {
        if (value - another < min)
            return Result.DENIED

        value -= another
        return Result.APPROVED
    }

}