package com.gadarts.demolition.core.components

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.gadarts.demolition.core.components.physics.MotionState

class PhysicsComponent : GameComponent() {
    private val localInertia = Vector3()
    private var motionState: MotionState? = null
    lateinit var rigidBody: btRigidBody

    override fun reset() {

    }

    fun init(
        colShape: btCollisionShape,
        mass: Float,
        transform: Matrix4?,
        flag: Int,
        angularFactor: Float,
    ) {
        if (transform != null) {
            motionState = MotionState()
            motionState!!.transformObject = transform
            motionState!!.setWorldTransform(transform)
        }
        calculateLocalInertia(colShape, mass)
        initializeBody(colShape, mass, transform, flag, angularFactor)
    }

    private fun initializeBody(
        colShape: btCollisionShape,
        mass: Float,
        transform: Matrix4?,
        flag: Int,
        angularFactor: Float,
    ) {
        this.rigidBody = btRigidBody(mass, motionState, colShape, localInertia)
        if (transform != null) {
            this.rigidBody.worldTransform = transform
        }
        activateBody()
        this.rigidBody.collisionFlags = flag
        rigidBody.setAngularFactor(angularFactor)
    }

    private fun activateBody() {
        rigidBody.setSleepingThresholds(1f, 1f)
        rigidBody.deactivationTime = 5f
        rigidBody.activate()
        rigidBody.activationState = Collision.DISABLE_DEACTIVATION
    }

    private fun calculateLocalInertia(
        collisionShape: btCollisionShape,
        mass: Float
    ) {
        if (mass == 0f) {
            localInertia.setZero()
        } else {
            collisionShape.calculateLocalInertia(mass, localInertia)
        }
    }

}
