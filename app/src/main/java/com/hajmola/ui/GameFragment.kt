package com.hajmola.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hajmola.BallView
import com.hajmola.R
import com.hajmola.game.GameState
import com.hajmola.game.Position
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var player1: PlayerView? = null
    private var player2: PlayerView? = null
    private var ball: ImageView? = null

    private val viewModel by viewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            player1 = findViewById(R.id.player1)
            player2 = findViewById(R.id.player2)
            ball = findViewById(R.id.ball)
        }

        player1?.transmitter = { x ,y ->
            viewModel.updatePlayerPosition(x, y,0)
        }

        if(arguments?.getInt("") == 1){
            player2?.transmitter = { x,y ->
                viewModel.updatePlayerPosition(x, y,1)
            }
        }
        observeForGameUpdate()
    }

    private fun observeForGameUpdate(){
        lifecycleScope.launch {
            viewModel.uiState.collect{
                when(it){
                    is GameState.Finished -> {
                        Toast.makeText(context, "${it.winner.name} Wins", Toast.LENGTH_LONG).show()
                    }
                    GameState.Initialized -> {
                    }
                    is GameState.OnGoing -> {
                        ball?.setPosition(it.ball)
                        player1?.setPosition(it.player1)
                        player2?.setPosition(it.player2)
                    }
                }
            }
        }
    }

    private fun View.setPosition(pos: Position){
        this.x = pos.x
        this.y = pos.y
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroy()
    }

    companion object {
        fun newInstance() =
            GameFragment().apply {}
    }
}