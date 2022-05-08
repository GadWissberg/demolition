package com.gadarts.demolition.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.assets.GameAssetManager

class InputSystem : GameEntitySystem() {
    private lateinit var debugInput: CameraInputController

    override fun initialize(am: GameAssetManager) {
        initializeInput()
    }

    private fun initializeInput() {
        if (DefaultGameSettings.DEBUG_INPUT) {
            debugInput = CameraInputController(commonData.camera)
            debugInput.autoUpdate = true
            Gdx.input.inputProcessor = debugInput
        } else {
            Gdx.input.inputProcessor = commonData.stage
        }
    }

    override fun resume(delta: Long) {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

}
