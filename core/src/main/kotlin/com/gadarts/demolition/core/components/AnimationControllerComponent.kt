package com.gadarts.demolition.core.components

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController

class AnimationControllerComponent : GameComponent() {

    lateinit var animationController: AnimationController

    override fun reset() {

    }

    fun init(modelInstance: ModelInstance) {
        animationController = AnimationController(modelInstance)
        animationController.allowSameAnimation = true
        animationController.setAnimation(modelInstance.animations[0].id, -1)
    }

}
