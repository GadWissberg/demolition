package com.gadarts.demolition.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.systems.physics.CollisionShapesDebugDrawing

class CommonData() : Disposable {


    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )
    val stage: Stage = Stage()
    var debugDrawingMethod: CollisionShapesDebugDrawing? = null
    var collisionWorld: btDiscreteDynamicsWorld? = null
    override fun dispose() {
        stage.dispose()
    }

    companion object {
        const val FOV = 67F
        const val CHAIN_COLLISION_SHAPE_RADIUS = 0.03F
        const val CHAIN_COLLISION_SHAPE_HEIGHT = 0.15F
        const val CRANE_SHAPE_WIDTH = 1F
        const val CRANE_SHAPE_HEIGHT = 0.05F
        const val CRANE_SHAPE_DEPTH = 0.12F
        const val CRANE_CONST_REL_POINT_X = CRANE_SHAPE_WIDTH - 0.1F
        const val CRANE_CONST_REL_POINT_Y = -0.01F
    }
}
