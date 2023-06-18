package com.hajmola.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.hajmola.B2dFun.PhysicsConstants
import com.hajmola.UserData
import kotlin.experimental.or

interface WorldSetter {
    fun setUpWorld(world: World)
}

val Float.half: Float
    get() = this / 2

class SimpleWorldSetter(): WorldSetter{
    override fun setUpWorld(world: World) {
        world.apply { 
            createTable(this)
            addStricker(this)
            addStricker(this)
            addPuck(this)
        }
    }
    
    private fun createTable(world: World) {
        val table = listOf(
            WallDef(
                Vector2(PhysicsConstants.X_START, PhysicsConstants.Y_CENTER),
                Dimen(
                    PhysicsConstants.WALL_THICKNESS,
                    PhysicsConstants.verticalWallHalfHeight
                )
            ),
            WallDef(
                Vector2(PhysicsConstants.X_END, PhysicsConstants.Y_CENTER),
                Dimen(
                    PhysicsConstants.WALL_THICKNESS,
                    PhysicsConstants.verticalWallHalfHeight
                )
            ),
            WallDef(
                Vector2(
                    PhysicsConstants.HorizontalWallLeftX,
                    PhysicsConstants.Y_START
                ),
                Dimen(
                    PhysicsConstants.HorizontalWallHalfWidth,
                    PhysicsConstants.WALL_THICKNESS
                )
            ),
            WallDef(
                Vector2(
                    PhysicsConstants.GoalX,
                    PhysicsConstants.Y_START - PhysicsConstants.GOAL_HEIGHT.half
                ),
                Dimen(
                    PhysicsConstants.GOAL_WIDTH.half,
                    PhysicsConstants.GOAL_HEIGHT
                ),
                categoryBits = PhysicsConstants.BIT_GOAL
            ),
            WallDef(
                Vector2(
                    PhysicsConstants.HorizontalWallRightX,
                    PhysicsConstants.Y_START
                ),
                Dimen(
                    PhysicsConstants.HorizontalWallHalfWidth,
                    PhysicsConstants.WALL_THICKNESS
                )
            ),
            WallDef(
                Vector2(PhysicsConstants.HorizontalWallLeftX, PhysicsConstants.Y_END),
                Dimen(
                    PhysicsConstants.HorizontalWallHalfWidth,
                    PhysicsConstants.WALL_THICKNESS
                )
            ),
            WallDef(
                Vector2(
                    PhysicsConstants.HorizontalWallRightX,
                    PhysicsConstants.Y_END
                ),
                Dimen(
                    PhysicsConstants.HorizontalWallHalfWidth,
                    PhysicsConstants.WALL_THICKNESS
                )
            ),
            WallDef(
                Vector2(
                    PhysicsConstants.GoalX,
                    PhysicsConstants.Y_END + PhysicsConstants.GOAL_HEIGHT.half
                ),
                Dimen(
                    PhysicsConstants.GOAL_WIDTH.half,
                    PhysicsConstants.GOAL_HEIGHT.half
                ),
                categoryBits = PhysicsConstants.BIT_GOAL
            )
        )
        table.forEach {
            addWall(world, it.center, it.dimen, it.maskBits, it.categoryBits ?: PhysicsConstants.BIT_WALL)
        }

//        //PlayerControls
//        //Goals
//        addWall(pos = Vector2(GoalX, 0f), Dimen(0.5f, 0.02f), BIT_PLAYER, false)
//        addWall(pos = Vector2(GoalX, 1f), Dimen(0.5f, 0.02f), BIT_PLAYER, false)
//        addWall(pos = Vector2(GoalX, Y_CENTER), Dimen(0.5f, 0.02f), BIT_PLAYER, false)
    }

    private fun addStricker(world: World): Body {
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
        fdef.filter.maskBits = PhysicsConstants.BIT_BALL
        fdef.shape = circleShape
        puck.userData = UserData.PUCK.name
        puck.createFixture(fdef)
        return puck
    }

    private fun addPuck(world: World): Body? {
        val bdef = BodyDef()
        bdef.position.set(Vector2(0.1f, 0.1f))
        bdef.type = BodyDef.BodyType.DynamicBody
        val puck = world.createBody(bdef)
        val circleShape = CircleShape()
        circleShape.radius = 0.01f
        val fdef = FixtureDef()
        fdef.shape = circleShape
        fdef.friction=0f
        fdef.filter.categoryBits = PhysicsConstants.BIT_BALL
        fdef.filter.maskBits = PhysicsConstants.BIT_WALL.or(PhysicsConstants.BIT_BALL).or(
            PhysicsConstants.BIT_PLAYER
        )
        puck.userData = UserData.PUCK.name
        puck.createFixture(fdef)
        puck.setLinearVelocity(Vector2(5f, 10f))
        return puck
    }
    
    private fun addWall(
        world: World,
        pos: Vector2,
        dimen: Dimen,
        maskBits: Short? = null,
        categoryBits:Short = PhysicsConstants.BIT_WALL,
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
        //dimenMap[body] = dimen
        return body
    }
}

data class WallDef(
    val center: Vector2,
    val dimen: Dimen,
    val maskBits: Short? = null,
    val categoryBits: Short? = null
)

data class Dimen(val halfWidth: Float, val halfHeight: Float)