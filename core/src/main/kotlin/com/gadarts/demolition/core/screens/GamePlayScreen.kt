package com.gadarts.demolition.core.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.*
import com.gadarts.demolition.core.systems.camera.CameraSystem
import com.gadarts.demolition.core.systems.render.RenderSystem


/**
 * The screen of the game itself.
 */
class GamePlayScreen(
    private val assetsManager: GameAssetManager,
) : Screen {


    private var pauseTime: Long = 0
    private lateinit var data: CommonData
    private lateinit var engine: PooledEngine

    override fun show() {
        this.engine = PooledEngine()
        EntityBuilder.initialize(engine)
        data = CommonData(assetsManager)
        addSystems(data)
        engine.systems.forEach { (it as GameEntitySystem).initialize(assetsManager) }
    }

    private fun addSystems(data: CommonData) {
        addSystem(InputSystem(), data)
        addSystem(PhysicsSystem(), data)
        addSystem(MapSystem(), data)
        addSystem(RenderSystem(), data)
        addSystem(CameraSystem(), data)
    }

    private fun addSystem(system: GameEntitySystem, data: CommonData) {
        system.commonData = data
        system.assetsManager = assetsManager
        engine.addSystem(system)
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
        pauseTime = TimeUtils.millis()
    }

    override fun resume() {
        val delta = TimeUtils.timeSinceMillis(pauseTime)
        engine.systems.forEach { (it as GameEntitySystem).resume(delta) }
    }

    override fun hide() {
    }

    override fun dispose() {
        engine.systems.forEach { (it as GameEntitySystem).dispose() }
        data.dispose()
    }


}