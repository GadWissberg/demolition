package com.gadarts.demolition.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.physics.CollisionShapesDebugDrawing

class CommonData(assetsManager: GameAssetManager) : Disposable {
    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )
    val stage: Stage = Stage()
    var debugDrawingMethod: CollisionShapesDebugDrawing? = null
    var collisionWorld: btDiscreteDynamicsWorld? = null


    companion object {
        const val FOV = 67F
    }

    override fun dispose() {
        stage.dispose()
    }
}
