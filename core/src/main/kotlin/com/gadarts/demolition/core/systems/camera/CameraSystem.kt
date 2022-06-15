package com.gadarts.demolition.core.systems.camera

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.CommonData
import com.gadarts.demolition.core.systems.CommonData.Companion.MAP_SIZE
import com.gadarts.demolition.core.systems.GameEntitySystem

class CameraSystem : GameEntitySystem<CameraSystemEventsSubscriber>() {

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
        placeAndRotateCamera()
    }

    private fun placeAndRotateCamera() {
        commonData.camera.position.set(INITIAL_X, INITIAL_Y, INITIAL_Z)
        commonData.camera.rotate(Vector3.X, -45F)
        commonData.camera.rotate(Vector3.Y, 90F)
        commonData.camera.lookAt(
            auxVector.set(
                MAP_SIZE / 2F,
                0F,
                MAP_SIZE / 2F
            )
        )
    }

    companion object {
        private val auxVector = Vector3()
        private const val NEAR = 0.1F
        private const val FAR = 300F
        private const val INITIAL_Y = 3F
        private const val INITIAL_X = MAP_SIZE / 2F + 5F
        private const val INITIAL_Z = MAP_SIZE / 2F
    }

    override val subscribers: HashSet<CameraSystemEventsSubscriber> = HashSet()

}