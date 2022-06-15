package com.gadarts.demolition.core.components

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.gadarts.demolition.core.components.child.ChildDecalComponent

object ComponentsMapper {
    val modelInstance: ComponentMapper<ModelInstanceComponent> =
        ComponentMapper.getFor(ModelInstanceComponent::class.java)
    val physics: ComponentMapper<PhysicsComponent> =
        ComponentMapper.getFor(PhysicsComponent::class.java)
    val animationController: ComponentMapper<AnimationControllerComponent> =
        ComponentMapper.getFor(AnimationControllerComponent::class.java)

}
