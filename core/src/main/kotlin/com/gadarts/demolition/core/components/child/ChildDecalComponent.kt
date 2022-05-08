package com.gadarts.demolition.core.components.child

import com.gadarts.demolition.core.components.GameComponent

class ChildDecalComponent : GameComponent() {
    private var animateRotation: Boolean = false
    var decals = ArrayList<ChildDecal>()

    fun init(decals: List<ChildDecal>, animateRotation: Boolean) {
        this.decals.clear()
        this.decals.addAll(decals)
        this.animateRotation = animateRotation
    }

    override fun reset() {
    }

}
