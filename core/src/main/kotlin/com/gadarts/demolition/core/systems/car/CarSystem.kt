package com.gadarts.demolition.core.systems.car

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.assets.ModelsDefinitions
import com.gadarts.demolition.core.systems.GameEntitySystem

class CarSystem : GameEntitySystem<CarSystemEventsSubscriber>() {

    override val subscribers: HashSet<CarSystemEventsSubscriber> = HashSet()

    override fun initialize(am: GameAssetManager) {
        val collisionShape = createCarCollisionShape()
        val model = am.getAssetByDefinition(ModelsDefinitions.CAR)
        addCar(model, collisionShape, auxVector.set(2F, 2F, -1F))
        addCar(model, collisionShape, auxVector.set(2F, 2F, 1F))
        addCar(model, collisionShape, auxVector.set(2F, 2F, 0F))
    }

    private fun addCar(
        model: Model,
        collisionShape: btCollisionShape,
        position: Vector3
    ) {
        val modelInstance = ModelInstance(model)
        EntityBuilder.begin()
            .addModelInstanceComponent(
                modelInstance,
                position
            ).addPhysicsComponent(collisionShape, modelInstance.transform, 20F, CF_CHARACTER_OBJECT)
            .finishAndAddToEngine()
    }

    private fun createCarCollisionShape(): btCollisionShape {
        val collisionShape = btCompoundShape()
        val shape1 = btCapsuleShape(0.2F, 1.9F)
        collisionShape.addChildShape(
            auxMatrix.rotate(Vector3.Z, 90F).translate(-0.1F, -0.1F, 0F),
            shape1
        )
        val shape2 = btCapsuleShape(0.2F, 1.9F)
        collisionShape.addChildShape(
            auxMatrix.idt().translate(0F, 0F, -0.25F).rotate(Vector3.Z, 90F)
                .translate(-0.1F, -0.1F, 0F), shape2
        )
        val shape3 = btCapsuleShape(0.2F, 1.9F)
        collisionShape.addChildShape(
            auxMatrix.idt().translate(0F, 0F, 0.25F).rotate(Vector3.Z, 90F)
                .translate(-0.1F, -0.1F, 0F), shape3
        )
        val shape4 = btCapsuleShape(0.1F, 0.4F)
        collisionShape.addChildShape(
            auxMatrix.idt().rotate(Vector3.Z, 90F).translate(0.25F, 0.1F, 0F), shape4
        )
        val shape5 = btCapsuleShape(0.1F, 0.4F)
        collisionShape.addChildShape(
            auxMatrix.idt().translate(0F, 0F, -0.25F).rotate(Vector3.Z, 90F)
                .translate(0.25F, 0.1F, 0F), shape5
        )
        val shape6 = btCapsuleShape(0.1F, 0.4F)
        collisionShape.addChildShape(
            auxMatrix.idt().translate(0F, 0F, 0.25F).rotate(Vector3.Z, 90F)
                .translate(0.25F, 0.1F, 0F), shape6
        )
        return collisionShape
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
    }

    companion object {
        val auxVector = Vector3()
        val auxMatrix = Matrix4()
    }
}
