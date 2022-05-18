package com.gadarts.demolition.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.systems.physics.CollisionShapesDebugDrawing

class CommonData() : Disposable {


    lateinit var directionsMapping: Array<Array<Vector3>>
    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )
    val stage: Stage = Stage()
    var debugDrawingMethod: CollisionShapesDebugDrawing? = null
    lateinit var collisionWorld: btDiscreteDynamicsWorld

    override fun dispose() {
        stage.dispose()
        collisionWorld.dispose()
    }

    companion object {
        const val FOV = 67F
        const val CHAIN_COLLISION_SHAPE_RADIUS = 0.03F
        const val CHAIN_COLLISION_SHAPE_HEIGHT = 0.15F
        const val CRANE_SHAPE_HALF_WIDTH = 0.9F
        const val CRANE_SHAPE_HALF_HEIGHT = 0.05F
        const val CRANE_SHAPE_HALF_DEPTH = 0.08F
        const val MAP_SIZE = 10F
    }
}
