package com.hajmola.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.hajmola.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameImpl(private val worldSetter: WorldSetter) : Game {

    private val world: World = World(Vector2(0f, 0f), true).apply {
        setContinuousPhysics(true)
    }

    private var gameThread: Job? = null

    private var isGameStillOn = false

    private var player1: Position = Position(0f, 0f)
    private var player2: Position = Position(0f, 0f)
    private var ball: Position = Position(0f, 0f)

    private val someScope =  CoroutineScope(Dispatchers.Unconfined)
    var winner: UserData = UserData.PLAYER2
    private val gameStateFlow = MutableStateFlow<GameState>(GameState.Initialized)
    val gameJob = someScope.launch {
        while (isGameStillOn) {
            world.step(0f, 0, 0)
            getBodies().forEach {
                when (it.userData) {
                    UserData.PUCK.name -> {
                        it.position.apply {
                            ball = Position(x, y)
                        }
                    }

                    UserData.PLAYER2.name -> {
                        it.position.apply {
                            player2 = Position(x, y)
                        }
                    }

                    UserData.PLAYER.name -> {
                        it.position.apply {
                            player1 = Position(x, y)
                        }
                    }
                }
            }
            gameStateFlow.emit(GameState.OnGoing(player1.copy(), player2.copy(), ball.copy()))
            delay(1000) // TODO set this according to 60fps
        }
        gameStateFlow.emit(GameState.Finished(winner))
    }
    val gameInputStateFlow = MutableStateFlow<GameInput?>(null)

    override fun initialize() {
        worldSetter.setUpWorld(world)
        gameThread = gameJob
        gameThread?.start()
        observeForGameUpdate()
    }
    override fun updateGame(input: GameInput) {
        someScope.launch {
            gameInputStateFlow.emit(input)
        }
    }
    override fun getCurrentGameStateFlow(): StateFlow<GameState> = gameStateFlow
    override fun destroy() {
        gameThread?.cancel()
        someScope.cancel()
        world.dispose()
    }

    private fun observeForGameUpdate() {
        someScope.launch {
            gameInputStateFlow.collect{ input ->
                if(input == null){}
                else getBodies().forEach {
                    when (it.userData) {
                        UserData.PLAYER -> input.player1?.let { it1 -> updatePosition(it, it1) }
                        UserData.PLAYER2 -> input.player2?.let { it1 -> updatePosition(it, it1) }
                    }
                }
            }
        }
    }
    private fun updatePosition(body: Body, position: Position) {
        body.apply {
            setTransform(Vector2(position.x, position.y), 0f)
            setLinearVelocity(0f, 0f)
        }
    }
    private fun getBodies() = com.badlogic.gdx.utils.Array<Body>().apply {
        world.getBodies(this)
    }
}