package com.gadarts.demolition.core.systems.camera

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier

class CameraSystem : GameEntitySystem(), Notifier<CameraSystemEventsSubscriber> {

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
        commonData.camera.position.set(INITIAL_X, INITIAL_Y, 0F)
        commonData.camera.rotate(Vector3.X, -45F)
        commonData.camera.rotate(Vector3.Y, 90F)
    }

    companion object {
        const val NEAR = 0.1F
        const val FAR = 300F
        const val INITIAL_Y = 4F
        const val INITIAL_X = 3F
    }

    override val subscribers: HashSet<CameraSystemEventsSubscriber> = HashSet()

}