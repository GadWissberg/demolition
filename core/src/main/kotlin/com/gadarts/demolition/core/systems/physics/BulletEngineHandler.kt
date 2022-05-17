package com.gadarts.demolition.core.systems.physics

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.CommonData

class BulletEngineHandler : Disposable, EntityListener {

    private lateinit var debugDrawer: DebugDrawer
    private lateinit var broadPhase: btAxisSweep3
    private lateinit var ghostPairCallback: btGhostPairCallback
    private lateinit var solver: btSequentialImpulseConstraintSolver
    private lateinit var dispatcher: btCollisionDispatcher
    private lateinit var collisionConfiguration: btDefaultCollisionConfiguration
    lateinit var collisionWorld: btDiscreteDynamicsWorld

    private fun initializeDebug() {
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
        collisionWorld.debugDrawer = debugDrawer
    }

    private fun initializeBroadPhase() {
        ghostPairCallback = btGhostPairCallback()
        val corner1 = Vector3(-100F, -100F, -100F)
        val corner2 = Vector3(100F, 100F, 100F)
        broadPhase = btAxisSweep3(corner1, corner2)
        broadPhase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
    }

    override fun entityAdded(entity: Entity?) {
        if (ComponentsMapper.physics.has(entity)) {
            val btRigidBody: btRigidBody = ComponentsMapper.physics.get(entity).rigidBody
            collisionWorld.addRigidBody(btRigidBody)
        }
    }

    override fun entityRemoved(entity: Entity?) {
        if (ComponentsMapper.physics.has(entity)) {
            val body: btRigidBody = ComponentsMapper.physics[entity].rigidBody
            body.activationState = 0
            collisionWorld.removeCollisionObject(body)
        }
    }

    fun initialize(commonData: CommonData, engine: Engine) {
        Bullet.init()
        collisionConfiguration = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfiguration)
        solver = btSequentialImpulseConstraintSolver()
        initializeBroadPhase()
        initializeCollisionWorld()
        initializeDebug()
        commonData.debugDrawingMethod = object : CollisionShapesDebugDrawing {
            override fun drawCollisionShapes(camera: PerspectiveCamera) {
                debugDrawer.begin(camera)
                collisionWorld.debugDrawWorld()
                debugDrawer.end()
            }
        }
        engine.addEntityListener(this)
    }

    private fun initializeCollisionWorld() {
        collisionWorld = btDiscreteDynamicsWorld(
            dispatcher,
            broadPhase,
            solver,
            collisionConfiguration
        )
        collisionWorld.gravity = Vector3(0F, GRAVITY_FORCE, 0F)
    }

    override fun dispose() {
        collisionConfiguration.dispose()
        solver.dispose()
        dispatcher.dispose()
        ghostPairCallback.dispose()
        broadPhase.dispose()
        debugDrawer.dispose()
        collisionWorld.dispose()
    }

    fun update(deltaTime: Float) {
        collisionWorld.stepSimulation(
            deltaTime,
            5,
            1f / DefaultGameSettings.FPS_TARGET
        )
    }


    companion object {
        const val GRAVITY_FORCE = -9.8f
    }

}
