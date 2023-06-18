package com.hajmola.game

import kotlinx.coroutines.flow.StateFlow

interface Game {

    fun initialize()

    fun updateGame(input: GameInput)

    fun getCurrentGameStateFlow(): StateFlow<GameState>

    fun destroy()
}