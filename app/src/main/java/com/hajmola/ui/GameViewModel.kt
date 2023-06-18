package com.hajmola.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hajmola.game.Game
import com.hajmola.game.GameImpl
import com.hajmola.game.GameInput
import com.hajmola.game.GameState
import com.hajmola.game.Position
import com.hajmola.game.SimpleWorldSetter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {
    fun updatePlayerPosition(x: Float, y: Float, player: Int) {
        val logicalX = x
        val logicalY = y

        val gameInput = if(player == 1 )GameInput(Position(logicalX, logicalY), null) else GameInput(null, Position(logicalX, logicalY))
        game.updateGame(gameInput)
    }

    val uiState = MutableStateFlow<GameState>(GameState.Initialized)

    private val game: Game = GameImpl(worldSetter = SimpleWorldSetter())

    init {
        game.initialize()
        observeForGameUpdates()
    }

    private fun observeForGameUpdates(){
        viewModelScope.launch {
            game.getCurrentGameStateFlow().collect{
                //process it. 
                uiState.emit(it)
            }
        }
    }

    fun onDestroy(){
        game.destroy()
    }
}