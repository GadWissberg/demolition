package com.gadarts.demolition.core.systems

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
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.physics.CollisionShapesDebugDrawing

class PhysicsSystem : GameEntitySystem(), EntityListener {
    private lateinit var debugDrawer: DebugDrawer
//    private lateinit var collisionWorld: btDiscreteDynamicsWorld
    private lateinit var broadPhase: btAxisSweep3
    private lateinit var ghostPairCallback: btGhostPairCallback
    private lateinit var solver: btSequentialImpulseConstraintSolver
    private lateinit var dispatcher: btCollisionDispatcher
    private lateinit var collisionConfiguration: btDefaultCollisionConfiguration

    override fun initialize(am: GameAssetManager) {
        Bullet.init()
        initializePhysics()
    }

    private fun initializePhysics() {
//        collisionConfiguration = btDefaultCollisionConfiguration()
//        dispatcher = btCollisionDispatcher(collisionConfiguration)
//        solver = btSequentialImpulseConstraintSolver()
//        initializeBroadPhase()
//        initializeCollisionWorld()
//        initializeDebug()
//        commonData.debugDrawingMethod = object : CollisionShapesDebugDrawing {
//            override fun drawCollisionShapes(camera: PerspectiveCamera) {
//                debugDrawer.begin(camera)
//                collisionWorld.debugDrawWorld()
//                debugDrawer.end()
//            }
//        }
//        engine.addEntityListener(this)
    }

    private fun initializeDebug() {
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
//        collisionWorld.debugDrawer = debugDrawer
    }

    override fun entityAdded(entity: Entity?) {
        if (ComponentsMapper.physics.has(entity)) {
            val btRigidBody: btRigidBody = ComponentsMapper.physics.get(entity).btRigidBody
//            collisionWorld.addRigidBody(btRigidBody)
        }
    }

    override fun entityRemoved(entity: Entity?) {
        if (ComponentsMapper.physics.has(entity)) {
            val body: btRigidBody = ComponentsMapper.physics[entity].btRigidBody
            body.activationState = 0
//            collisionWorld.removeCollisionObject(body)
        }
    }

    private fun initializeBroadPhase() {
        ghostPairCallback = btGhostPairCallback()
        val corner1 = Vector3(-100F, -100F, -100F)
        val corner2 = Vector3(100F, 100F, 100F)
        broadPhase = btAxisSweep3(corner1, corner2)
        broadPhase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
    }

    override fun resume(delta: Long) {
    }

//    private fun initializeCollisionWorld() {
//        collisionWorld = btDiscreteDynamicsWorld(
//            dispatcher,
//            broadPhase,
//            solver,
//            collisionConfiguration
//        )
//        collisionWorld.gravity = Vector3(0F, -9.8f, 0F)
//    }

    override fun update(deltaTime: Float) {
//        collisionWorld.stepSimulation(deltaTime, 5, 1f / DefaultGameSettings.FPS_TARGET)
    }

    override fun dispose() {
        collisionConfiguration.dispose()
        solver.dispose()
        dispatcher.dispose()
        ghostPairCallback.dispose()
        broadPhase.dispose()
//        collisionWorld.dispose()
        debugDrawer.dispose()
    }

}
