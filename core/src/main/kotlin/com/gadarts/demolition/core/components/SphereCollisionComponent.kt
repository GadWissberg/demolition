package com.gadarts.demolition.core.components

class SphereCollisionComponent : GameComponent() {
    var radius: Float = 0.0f

    override fun reset() {

    }

    fun init(radius: Float) {
        this.radius = radius
    }

}
