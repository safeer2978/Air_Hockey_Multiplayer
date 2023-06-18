package com.hajmola

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.hajmola.B2dFun.PhysicsConstants.GOAL_HEIGHT
import com.hajmola.B2dFun.PhysicsConstants.BIT_BALL
import com.hajmola.B2dFun.PhysicsConstants.BIT_GOAL
import com.hajmola.B2dFun.PhysicsConstants.BIT_PLAYER
import com.hajmola.B2dFun.PhysicsConstants.BIT_WALL
import com.hajmola.B2dFun.PhysicsConstants.GOAL_WIDTH
import com.hajmola.B2dFun.PhysicsConstants.GoalX
import com.hajmola.B2dFun.PhysicsConstants.HorizontalWallHalfWidth
import com.hajmola.B2dFun.PhysicsConstants.HorizontalWallLeftX
import com.hajmola.B2dFun.PhysicsConstants.HorizontalWallRightX
import com.hajmola.B2dFun.PhysicsConstants.WALL_THICKNESS
import com.hajmola.B2dFun.PhysicsConstants.X_END
import com.hajmola.B2dFun.PhysicsConstants.X_START
import com.hajmola.B2dFun.PhysicsConstants.Y_CENTER
import com.hajmola.B2dFun.PhysicsConstants.Y_END
import com.hajmola.B2dFun.PhysicsConstants.Y_START
import com.hajmola.B2dFun.PhysicsConstants.scale
import com.hajmola.B2dFun.PhysicsConstants.verticalWallHalfHeight
import kotlin.experimental.or

private val Float.half: Float
    get() = this / 2

class B2dFun {

    var world: World = World(Vector2(0f, 0f), true).apply {
        setContinuousPhysics(true)
    }


    data class WallDef(
        val center: Vector2,
        val dimen: Dimen,
        val maskBits: Short? = null,
        val categoryBits: Short? = null
    )

    data class Dimen(val halfWidth: Float, val halfHeight: Float)

    val positionMap = mutableMapOf<Body, Vector2>()
    val dimenMap = mutableMapOf<Body, Dimen>()


    fun addWall(
        pos: Vector2,
        dimen: Dimen,
        maskBits: Short? = null,
        categoryBits:Short = BIT_WALL,
        setRestitution: Boolean = true
    ): Body {
        val bdef = BodyDef()
        bdef.position.set(pos.x, pos.y)
        bdef.type = BodyDef.BodyType.StaticBody
        val body: Body = world.createBody(bdef)
        val shape = PolygonShape()
        shape.setAsBox(dimen.halfWidth, dimen.halfHeight)
        val fdef = FixtureDef()
        fdef.restitution = 1f
        fdef.shape = shape
        fdef.filter.categoryBits = categoryBits
        body.createFixture(fdef)
        body.userData = UserData.WALL.name
        dimenMap[body] = dimen
        return body
    }

    init {
        createTable()
        addPuck()
    }

    object PhysicsConstants{
        const val WALL_THICKNESS = 0.01f
        const val scale = 960f
        const val GOAL_WIDTH = 0.25f
        const val GOAL_HEIGHT = 0.05f
        const val SEGMENT = (1 - GOAL_WIDTH) / 4
        const val HorizontalWallHalfWidth = SEGMENT
        const val HorizontalWallLeftX = SEGMENT
        const val GoalX = (2 * SEGMENT) + (GOAL_WIDTH / 2)
        const val HorizontalWallRightX = (3 * SEGMENT) + GOAL_WIDTH
        const val verticalWallHalfHeight = 1f
        const val BIT_PLAYER: Short = 2
        const val BIT_BALL: Short = 1
        const val BIT_WALL: Short = 4
        const val BIT_GOAL: Short = 8
        const val X_START = 0f
        const val X_END = 1f
        const val Y_START = 0f
        const val Y_END = 2f
        const val Y_CENTER = Y_END / 2
        const val X_CENTER = X_END / 2
    }

    fun addPuck(): Body? {
        val bdef = BodyDef()
        bdef.position.set(Vector2(0.1f, 0.1f))
        bdef.type = BodyDef.BodyType.DynamicBody
        val puck = world.createBody(bdef)
        val circleShape = CircleShape()
        circleShape.radius = 0.01f
        val fdef = FixtureDef()
        fdef.shape = circleShape
        fdef.friction=0f
        fdef.filter.categoryBits = BIT_BALL
        fdef.filter.maskBits = BIT_WALL.or(BIT_BALL).or(BIT_PLAYER)
        puck.userData = UserData.PUCK.name
        puck.createFixture(fdef)
        puck.setLinearVelocity(Vector2(5f, 10f))
        return puck
    }

    val player: Body = addStricker()

    fun movePlayer(x:Float, y: Float){
        player.apply {
            setTransform(Vector2(x, Y_END - y), 0f)
            setLinearVelocity(0f,0f)
        }
    }

    fun addStricker(): Body {
        val bdef = BodyDef()
        bdef.position.set(Vector2(0.8f, 0.1f))
        bdef.type = BodyDef.BodyType.DynamicBody
        val puck = world.createBody(bdef)
        val circleShape = CircleShape()
        circleShape.radius = 0.08f
        val fdef = FixtureDef()
        fdef.restitution = 1f
        fdef.friction=0f
        fdef.density = 10000f
        fdef.filter.maskBits = BIT_BALL
        fdef.shape = circleShape
        puck.userData = UserData.PUCK.name
        puck.createFixture(fdef)
        return puck
    }


    class GoalContactListener(private val declareWinner: (user:UserData)->Unit): ContactListener{
        val someSet = hashSetOf(UserData.PUCK, UserData.GOAL)
        override fun beginContact(contact: Contact?) {
            if(contact?.fixtureA?.userData in someSet
                && contact?.fixtureB?.userData in someSet) {
                val goalFixture = if(contact?.fixtureA?.userData == UserData.PUCK) contact.fixtureB else contact?.fixtureA
                declareWinner.invoke(UserData.valueOf(goalFixture?.userData.toString()))
            }
        }

        override fun endContact(contact: Contact?) {
        }

        override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        }

        override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        }

    }


    fun createTable() {
        val table = listOf(
            WallDef(
                Vector2(X_START, Y_CENTER),
                Dimen(WALL_THICKNESS, verticalWallHalfHeight)
            ),
            WallDef(
                Vector2(X_END, Y_CENTER),
                Dimen(WALL_THICKNESS, verticalWallHalfHeight)
            ),
            WallDef(
                Vector2(HorizontalWallLeftX, Y_START),
                Dimen(HorizontalWallHalfWidth, WALL_THICKNESS)
            ),
            WallDef(
                Vector2(GoalX, Y_START-GOAL_HEIGHT.half),
                Dimen(GOAL_WIDTH.half, GOAL_HEIGHT),
                categoryBits = BIT_GOAL
            ),
            WallDef(
                Vector2(HorizontalWallRightX, Y_START),
                Dimen(HorizontalWallHalfWidth, WALL_THICKNESS)
            ),
            WallDef(
                Vector2(HorizontalWallLeftX, Y_END),
                Dimen(HorizontalWallHalfWidth, WALL_THICKNESS)
            ),
            WallDef(
                Vector2(HorizontalWallRightX, Y_END),
                Dimen(HorizontalWallHalfWidth, WALL_THICKNESS)
            ),
            WallDef(
                Vector2(GoalX, Y_END+GOAL_HEIGHT.half),
                Dimen(GOAL_WIDTH.half, GOAL_HEIGHT.half),
                categoryBits = BIT_GOAL
            )
        )
        table.forEach {
            addWall(it.center, it.dimen, it.maskBits, it.categoryBits ?: BIT_WALL)
        }

//        //PlayerControls
//        //Goals
//        addWall(pos = Vector2(GoalX, 0f), Dimen(0.5f, 0.02f), BIT_PLAYER, false)
//        addWall(pos = Vector2(GoalX, 1f), Dimen(0.5f, 0.02f), BIT_PLAYER, false)
//        addWall(pos = Vector2(GoalX, Y_CENTER), Dimen(0.5f, 0.02f), BIT_PLAYER, false)
    }

    fun stepInWorld(): Bitmap {
        val bitm = Bitmap.createBitmap(scale.toInt(), 2 * scale.toInt(), Bitmap.Config.ARGB_8888)
        val someCanvas = Canvas(bitm)
        val bodies = com.badlogic.gdx.utils.Array<Body>()
        world.step(0.002f, 1, 1)
        world.getBodies(bodies)
        bodies.forEach {
            renderBody(it, someCanvas)
        }
        return bitm
    }

    private fun renderBody(it: Body, someCanvas: Canvas) {
        val position = Vector2(it.position.x, PhysicsConstants.Y_END - it.position.y).scl(scale)
        val angle = it.angle
        when (it.userData) {
            UserData.WALL.name -> {
                val dimen = dimenMap[it]
                dimen?.let {
                    someCanvas.drawRect(
                        position.x.toLeft(dimen.halfWidth * scale),
                        position.y.toTop(dimen.halfHeight * scale),
                        position.x.toRight(dimen.halfWidth * scale),
                        position.y.toBottom(dimen.halfHeight * scale),
                        Paint().apply {
                            strokeWidth = 1f
                            color = Color.BLUE
                            style = Paint.Style.STROKE
                        })

                }
            }

            UserData.PUCK.name -> {
                Log.d("PUCK", it.position.toString())
                someCanvas.drawCircle(position.x, position.y, 40f, Paint().apply {
                    strokeWidth = 1f
                    color = Color.RED
                    style = Paint.Style.FILL
                })
            }

            else -> {}
        }
    }
}

fun Float.toTop(halfHeight: Float) = this + halfHeight
fun Float.toBottom(halfHeight: Float) = this - halfHeight
fun Float.toRight(halfWidth: Float) = this + halfWidth
fun Float.toLeft(halfWidth: Float) = this - halfWidth

enum class UserData {
    WALL,
    GOAL,
    A,B,
    PLAYER,
    PLAYER2,
    PUCK
}