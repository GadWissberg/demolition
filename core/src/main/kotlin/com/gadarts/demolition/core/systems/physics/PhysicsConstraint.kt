package com.gadarts.demolition.core.systems.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btPoint2PointConstraint
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody

class PhysicsConstraint(
    rigidBody: btRigidBody,
    rigidBody2: btRigidBody,
    point1: Vector3,
    point2: Vector3,
    val disableCollisionsBetweenLinkedBodies: Boolean = false
) : btPoint2PointConstraint(rigidBody, rigidBody2, point1, point2) {

}
