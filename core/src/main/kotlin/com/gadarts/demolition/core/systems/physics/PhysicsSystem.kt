package com.gadarts.demolition.core.systems.physics

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
import com.badlogic.gdx.physics.bullet.dynamics.btJointFeedback
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.CommonData
import com.gadarts.demolition.core.systems.CommonData.Companion.CHAIN_COLLISION_SHAPE_HEIGHT
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier
import com.gadarts.demolition.core.systems.player.PlayerSystemEventsSubscriber

class PhysicsSystem : GameEntitySystem(), EntityListener, Notifier<PhysicsSystemEventsSubscriber>,
    PlayerSystemEventsSubscriber {

    private lateinit var debugDrawer: DebugDrawer
    private lateinit var broadPhase: btAxisSweep3
    private lateinit var ghostPairCallback: btGhostPairCallback
    private lateinit var solver: btSequentialImpulseConstraintSolver
    private lateinit var dispatcher: btCollisionDispatcher
    private lateinit var collisionConfiguration: btDefaultCollisionConfiguration
    override val subscribers: HashSet<PhysicsSystemEventsSubscriber> = HashSet()
    private val constraints = ArrayList<PhysicsConstraint>()

    override fun initialize(am: GameAssetManager) {
        Bullet.init()
        initializePhysics()
    }

    private fun initializePhysics() {
        collisionConfiguration = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfiguration)
        solver = btSequentialImpulseConstraintSolver()
        initializeBroadPhase()
        initializeCollisionWorld()
        initializeDebug()
        commonData.debugDrawingMethod = object : CollisionShapesDebugDrawing {
            override fun drawCollisionShapes(camera: PerspectiveCamera) {
                debugDrawer.begin(camera)
                commonData.collisionWorld!!.debugDrawWorld()
                debugDrawer.end()
            }
        }
        engine.addEntityListener(this)
    }

    private fun initializeDebug() {
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
        commonData.collisionWorld!!.debugDrawer = debugDrawer
    }

    override fun entityAdded(entity: Entity?) {
        if (ComponentsMapper.physics.has(entity)) {
            val btRigidBody: btRigidBody = ComponentsMapper.physics.get(entity).rigidBody
            commonData.collisionWorld!!.addRigidBody(btRigidBody)
        }
    }

    override fun entityRemoved(entity: Entity?) {
        if (ComponentsMapper.physics.has(entity)) {
            val body: btRigidBody = ComponentsMapper.physics[entity].rigidBody
            body.activationState = 0
            commonData.collisionWorld!!.removeCollisionObject(body)
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

    private fun initializeCollisionWorld() {
        commonData.collisionWorld = btDiscreteDynamicsWorld(
            dispatcher,
            broadPhase,
            solver,
            collisionConfiguration
        )
        commonData.collisionWorld!!.gravity = Vector3(0F, -9.8f, 0F)
    }

    override fun update(deltaTime: Float) {
        commonData.collisionWorld!!.stepSimulation(
            deltaTime,
            5,
            1f / DefaultGameSettings.FPS_TARGET
        )
    }

    override fun dispose() {
        collisionConfiguration.dispose()
        solver.dispose()
        dispatcher.dispose()
        ghostPairCallback.dispose()
        broadPhase.dispose()
        commonData.collisionWorld?.dispose()
        debugDrawer.dispose()
        constraints.forEach { it.dispose() }
    }

    private fun addConstraintBetweenCraneAndChain(chain1: Entity, crane: Entity) {
        constraints.add(
            PhysicsConstraint(
                ComponentsMapper.physics.get(crane).rigidBody,
                ComponentsMapper.physics.get(chain1).rigidBody,
                auxVector1.set(CRANE_CONST_REL_POINT_X, CRANE_CONST_REL_POINT_Y, 0F),
                auxVector2.set(0F, CHAIN_COLLISION_SHAPE_HEIGHT / 3F, 0F),
                true
            )
        )
    }

    private fun addConstraintBetweenChains(
        chain1: Entity,
        chain2: Entity
    ) {
        val physicsConstraint = PhysicsConstraint(
            ComponentsMapper.physics.get(chain1).rigidBody,
            ComponentsMapper.physics.get(chain2).rigidBody,
            auxVector1.set(0F, -CHAIN_COLLISION_SHAPE_HEIGHT / 2F, 0F),
            auxVector2.set(0F, CHAIN_COLLISION_SHAPE_HEIGHT / 2F, 0F),
            true
        )
        constraints.add(physicsConstraint)
    }


    override fun onPlayerAdded(chains: List<Entity>, crane: Entity, ball: Entity) {
        addConstraintBetweenCraneAndChain(chains[0], crane)
        addConstraintBetweenChains(chains[0], chains[1])
        addConstraintBetweenChains(chains[1], chains[2])
        addConstraintBetweenChains(chains[2], chains[3])
        addConstraintBetweenChains(chains[3], chains[4])
        addConstraintBetweenChainAndBall(chains[4], ball)
        addConstraints()
    }

    private fun addConstraintBetweenChainAndBall(chain: Entity, ball: Entity) {
        constraints.add(
            PhysicsConstraint(
                ComponentsMapper.physics.get(chain).rigidBody,
                ComponentsMapper.physics.get(ball).rigidBody,
                auxVector1.set(0F, -CHAIN_COLLISION_SHAPE_HEIGHT, 0F),
                auxVector2.set(0F, 0.25F, 0F),
                true
            )
        )
    }

    private fun addConstraints() {
        constraints.forEach {
            commonData.collisionWorld!!.addConstraint(
                it,
                it.disableCollisionsBetweenLinkedBodies
            )
        }
    }

    companion object {
        val auxVector1 = Vector3()
        val auxVector2 = Vector3()
        const val CRANE_CONST_REL_POINT_X = CommonData.CRANE_SHAPE_HALF_WIDTH * 2F - 0.1F
        const val CRANE_CONST_REL_POINT_Y = CommonData.CRANE_SHAPE_HALF_WIDTH * 2F + 0.2F
    }
}
