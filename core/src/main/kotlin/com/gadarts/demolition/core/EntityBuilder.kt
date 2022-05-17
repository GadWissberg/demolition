package com.gadarts.demolition.core

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_STATIC_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btConvexShape
import com.gadarts.demolition.core.components.ModelInstanceComponent
import com.gadarts.demolition.core.components.PhysicsComponent

class EntityBuilder private constructor() {
    fun addModelInstanceComponent(
        model: Model,
        position: Vector3,
    ): EntityBuilder {
        val modelInstanceComponent = engine.createComponent(ModelInstanceComponent::class.java)
        modelInstanceComponent.init(model, position)
        entity!!.add(modelInstanceComponent)
        return instance
    }

    fun addModelInstanceComponent(model: ModelInstance, position: Vector3): EntityBuilder {
        val modelInstanceComponent = engine.createComponent(ModelInstanceComponent::class.java)
        modelInstanceComponent.init(model, position)
        entity!!.add(modelInstanceComponent)
        return instance
    }

    fun finishAndAddToEngine(): Entity {
        engine.addEntity(entity)
        val result = entity
        entity = null
        return result!!
    }

    fun addPhysicsComponent(
        collisionShape: btCollisionShape,
        transform: Matrix4? = null,
        mass: Float = 0F,
        collisionFilterFlag: Int = CF_KINEMATIC_OBJECT
    ): EntityBuilder {
        val component = engine.createComponent(PhysicsComponent::class.java)
        component.init(
            collisionShape,
            mass,
            transform,
            collisionFilterFlag
        )
        entity!!.add(component)
        return instance
    }

    companion object {

        private lateinit var instance: EntityBuilder
        var entity: Entity? = null
        lateinit var engine: PooledEngine

        fun begin(): EntityBuilder {
            entity = engine.createEntity()
            return instance
        }

        fun initialize(engine: PooledEngine) {
            Companion.engine = engine
            instance = EntityBuilder()
        }
    }
}
