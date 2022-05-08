package com.gadarts.demolition.core.systems.camera

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.GameEntitySystem

class CameraSystem : GameEntitySystem() {

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        commonData.camera.update()
    }


    override fun initialize(am: GameAssetManager) {
        initializeCamera()
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
    }

    private fun initializeCamera() {
        commonData.camera.near = NEAR
        commonData.camera.far = FAR
        commonData.camera.update()
        commonData.camera.position.set(0F, INITIAL_Y, INITIAL_Z)
        commonData.camera.rotate(Vector3.X, -45F)
    }

    companion object {
        const val NEAR = 0.1F
        const val FAR = 300F
        const val INITIAL_Y = 7F
        const val INITIAL_Z = 5F
    }

}