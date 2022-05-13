package com.gadarts.demolition.core.systems.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier

class InputSystem : GameEntitySystem(), Notifier<InputSystemEventsSubscriber> {

    private lateinit var debugInput: CameraInputController
    override val subscribers: HashSet<InputSystemEventsSubscriber> = HashSet()

    override fun initialize(am: GameAssetManager) {
        initializeInput()
    }

    private fun initializeInput() {
        if (DefaultGameSettings.DEBUG_INPUT) {
            debugInput = CameraInputController(commonData.camera)
            debugInput.autoUpdate = true
            Gdx.input.inputProcessor = debugInput
        } else {
            Gdx.input.inputProcessor = InputMultiplexer()
        }
        subscribers.forEach{it.onInputInitialized()}
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
    }

}
