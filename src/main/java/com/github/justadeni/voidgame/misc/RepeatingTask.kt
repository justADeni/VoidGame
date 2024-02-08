package com.github.justadeni.voidgame.misc

import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RepeatingTask(private val task: () -> Unit, private val everyMillis: Long) {

    private lateinit var job: Job

    fun start() {
        if (this::job.isInitialized)
            job.cancel()

        job = Scopes.defaultScope.launch {
            while (true) {
                delay(everyMillis)
                task.invoke()
            }
        }
    }

    fun stop() = job.cancel()

}