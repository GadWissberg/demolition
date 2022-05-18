package com.gadarts.demolition.core.systems.car

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.assets.ModelsDefinitions
import com.gadarts.demolition.core.components.CarComponent
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.CommonData
import com.gadarts.demolition.core.systems.CommonData.Companion.MAP_SIZE
import com.gadarts.demolition.core.systems.GameEntitySystem


class CarSystem : GameEntitySystem<CarSystemEventsSubscriber>() {

    private lateinit var carsEntities: ImmutableArray<Entity>
    override val subscribers: HashSet<CarSystemEventsSubscriber> = HashSet()
    private val rayFrom = Vector3()
    private val rayTo = Vector3()
    private lateinit var callback: ClosestRayResultCallback

    override fun initialize(am: GameAssetManager) {
        callback = ClosestRayResultCallback(rayFrom, rayTo)
        val collisionShape = createCarCollisionShape()
        val model = am.getAssetByDefinition(ModelsDefinitions.CAR)
        addCar(
            model,
            collisionShape,
            auxVector.set(MAP_SIZE / 2F + 2F, 4F, MAP_SIZE / 2F + 2F)
        )
        carsEntities = engine.getEntitiesFor(Family.all(CarComponent::class.java).get())
    }

    private fun addCar(
        model: Model,
        collisionShape: btCollisionShape,
        position: Vector3
    ) {
        val modelInstance = ModelInstance(model)
        modelInstance.transform.rotate(Vector3.Y, 90F).trn(position)
        EntityBuilder.begin()
            .addModelInstanceComponent(
                modelInstance,
            ).addPhysicsComponent(
                collisionShape,
                modelInstance.transform,
                CAR_MASS,
                CF_CHARACTER_OBJECT,
            )
            .addCarComponent()
            .finishAndAddToEngine()
    }

    override fun update(deltaTime: Float) {
        for (car in carsEntities) {
            val rigidBody = ComponentsMapper.physics.get(car).rigidBody
            val motionState = rigidBody.motionState
            motionState.getWorldTransform(auxMatrix)
            val pos = auxMatrix.getTranslation(auxVector)
            if (checkIfOnGround(car)) {
                handleCarMovement(pos, car, rigidBody)
            }
        }
    }

    private fun handleCarMovement(
        pos: Vector3,
        car: Entity,
        rigidBody: btRigidBody
    ) {
        val row = MathUtils.clamp(pos.z.toInt(), 0, MAP_SIZE.toInt())
        val col = MathUtils.clamp(pos.x.toInt(), 0, MAP_SIZE.toInt())
        val carComponent = ComponentsMapper.car.get(car)
        carComponent.desiredVelocity.set(commonData.directionsMapping[row][col])
        rigidBody.motionState.getWorldTransform(auxMatrix)
        auxVector.set(Vector3.X).rot(auxMatrix).nor()
        rigidBody.linearVelocity = auxVector.set(auxVector.x, 0F, auxVector.z).scl(CAR_SPEED)
        handleRotation(rigidBody, carComponent)
    }

    private fun handleRotation(rigidBody: btRigidBody, carComponent: CarComponent) {
        rigidBody.motionState.getWorldTransform(auxMatrix)
        auxVector.set(Vector3.X).rot(auxMatrix).nor()
        val xEqual = MathUtils.isEqual(auxVector.x, carComponent.desiredVelocity.x, 0.1F)
        val zEqual = MathUtils.isEqual(auxVector.z, carComponent.desiredVelocity.z, 0.1F)
        if (xEqual && zEqual) {
            rigidBody.angularVelocity = Vector3.Zero
        } else {
            rigidBody.applyTorqueImpulse(auxVector.set(Vector3.Y).scl(0.8F))
        }
    }

    private fun initializeRayForTest(character: Entity) {
        ComponentsMapper.physics[character].rigidBody.motionState.getWorldTransform(auxMatrix)
        auxMatrix.getTranslation(rayFrom)
        auxVector.set(0F, -1F, 0F).rot(auxMatrix)
        rayTo.set(auxVector).scl(0.5F).add(rayFrom)
        callback.collisionObject = null
        callback.closestHitFraction = 1F
    }

    private fun checkIfOnGround(character: Entity): Boolean {
        initializeRayForTest(character)
        commonData.collisionWorld.rayTest(rayFrom, rayTo, callback)
        return callback.hasHit()
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
        callback.dispose()
    }

    companion object {
        private val auxVector = Vector3()
        private val auxMatrix = Matrix4()
        private val auxQuat = Quaternion()
        const val CAR_MASS = 20F
        const val CAR_SPEED = 4F
    }
}
