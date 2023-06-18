package com.hajmola.game

import com.hajmola.UserData

data class Position(
    val x: Float,
    val y: Float
)

data class GameInput(
    val player1: Position?,
    val player2: Position?
)

sealed class GameState {
    object Initialized : GameState()
    data class OnGoing(val player1: Position, val player2: Position, val ball: Position) :
        GameState()

    data class Finished(val winner: UserData) : GameState()
}