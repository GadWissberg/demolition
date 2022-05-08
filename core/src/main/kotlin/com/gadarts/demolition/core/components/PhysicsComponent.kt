package com.gadarts.demolition.core.components

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.gadarts.demolition.core.components.physics.MotionState

class PhysicsComponent : GameComponent() {
    private val localInertia = Vector3()
    private lateinit var motionState: MotionState

    lateinit var btRigidBody: btRigidBody

    override fun reset() {

    }

    fun init(
        colShape: btCollisionShape,
        mass: Float,
        transform: Matrix4,
        flag: Int
    ) {
        if (motionState == null && mass > 0) {
            motionState = MotionState()
        }
        insertTransformObjectToMotionState(transform)
        calculateLocalInertia(colShape, mass)
        initializeBody(colShape, mass, transform, flag)
    }

    private fun initializeBody(
        colShape: btCollisionShape,
        mass: Float,
        transform: Matrix4,
        flag: Int
    ) {
        if (btRigidBody != null) {
            redefineRigidBody(colShape, mass)
        } else {
            btRigidBody = btRigidBody(mass, motionState, colShape, localInertia)
        }
        if (motionState != null) {
            btRigidBody.worldTransform = transform
            activateBody()
        }
        btRigidBody.collisionFlags = flag
    }
    private fun redefineRigidBody(
        colShape: btCollisionShape,
        mass: Float
    ) {
        disposeCollisionShape()
        btRigidBody.collisionShape = colShape
        btRigidBody.setMassProps(mass, localInertia)
    }
    private fun disposeCollisionShape() {
        val oldCollisionShape = btRigidBody.collisionShape
        oldCollisionShape?.dispose()
    }

    private fun activateBody() {
        btRigidBody.setSleepingThresholds(1f, 1f)
        btRigidBody.deactivationTime = 5f
        btRigidBody.activate()
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

    private fun insertTransformObjectToMotionState(transform: Matrix4?) {
        if (motionState != null) {
            if (transform != null) {
                motionState.setWorldTransform(transform)
            }
        }
    }

    fun replaceRigidBody(btRigidBody: btRigidBody) {
        if (btRigidBody != null) {
            btRigidBody.dispose()
        }
        this.btRigidBody = btRigidBody
    }

}
