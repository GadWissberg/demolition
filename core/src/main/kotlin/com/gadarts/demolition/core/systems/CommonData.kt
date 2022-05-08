package com.gadarts.demolition.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.assets.TexturesDefinitions.JOYSTICK
import com.gadarts.demolition.core.assets.TexturesDefinitions.JOYSTICK_CENTER
import com.gadarts.demolition.core.systems.physics.CollisionShapesDebugDrawing

class CommonData(assetsManager: GameAssetManager) : Disposable {
    private var touchpad: Touchpad
    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )
    val stage: Stage = Stage()
    var debugDrawingMethod: CollisionShapesDebugDrawing? = null

    init {
        val joystickTexture = assetsManager.getAssetByDefinition(JOYSTICK)
        val joystickDrawableTex = TextureRegionDrawable(joystickTexture)
        val joystickCenterTex =
            TextureRegionDrawable(assetsManager.getAssetByDefinition(JOYSTICK_CENTER))
        touchpad = Touchpad(
            DEAD_ZONE,
            Touchpad.TouchpadStyle(joystickDrawableTex, joystickCenterTex)
        )
    }


    companion object {
        const val DEAD_ZONE = 15F
        const val FOV = 67F
    }

    override fun dispose() {
        stage.dispose()
    }
}
