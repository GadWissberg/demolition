package com.gadarts.demolition.core.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.*
import com.gadarts.demolition.core.systems.camera.CameraSystem
import com.gadarts.demolition.core.systems.camera.CameraSystemEventsSubscriber
import com.gadarts.demolition.core.systems.input.InputSystem
import com.gadarts.demolition.core.systems.input.InputSystemEventsSubscriber
import com.gadarts.demolition.core.systems.map.MapSystem
import com.gadarts.demolition.core.systems.map.MapSystemEventsSubscriber
import com.gadarts.demolition.core.systems.physics.PhysicsSystem
import com.gadarts.demolition.core.systems.physics.PhysicsSystemEventsSubscriber
import com.gadarts.demolition.core.systems.player.PlayerSystem
import com.gadarts.demolition.core.systems.player.PlayerSystemEventsSubscriber
import com.gadarts.demolition.core.systems.profiling.ProfilingSystem
import com.gadarts.demolition.core.systems.profiling.ProfilingSystemEventsSubscriber
import com.gadarts.demolition.core.systems.render.RenderSystem
import com.gadarts.demolition.core.systems.render.RenderSystemEventsSubscriber


/**
 * The screen of the game itself.
 */
class GamePlayScreen(
    private val assetsManager: GameAssetManager,
) : Screen {

    private var pauseTime: Long = 0
    private lateinit var data: CommonData
    private lateinit var engine: PooledEngine
    private val systems: Map<Class<out SystemEventsSubscriber>, Class<out GameEntitySystem>> =
        mapOf(
            CameraSystemEventsSubscriber::class.java to CameraSystem::class.java,
            PhysicsSystemEventsSubscriber::class.java to PhysicsSystem::class.java,
            InputSystemEventsSubscriber::class.java to InputSystem::class.java,
            RenderSystemEventsSubscriber::class.java to RenderSystem::class.java,
            ProfilingSystemEventsSubscriber::class.java to ProfilingSystem::class.java,
            MapSystemEventsSubscriber::class.java to MapSystem::class.java,
            PlayerSystemEventsSubscriber::class.java to PlayerSystem::class.java,
        )

    override fun show() {
        this.engine = PooledEngine()
        EntityBuilder.initialize(engine)
        data = CommonData(assetsManager)
        addSystems()
        engine.systems.forEach { (it as GameEntitySystem).initialize(assetsManager) }
    }

    private fun addSystems() {
        systems.forEach { addSystem(it.value.newInstance()) }
        engine.systems.forEach {
            it.javaClass.interfaces.forEach { interfaze ->
                if (systems.containsKey(interfaze)) {
                    val system = engine.getSystem(systems[interfaze])
                    val notifier = system as Notifier<SystemEventsSubscriber>
                    notifier.subscribeForEvents(it as GameEntitySystem)
                }
            }
        }
    }

    private fun addSystem(system: GameEntitySystem) {
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