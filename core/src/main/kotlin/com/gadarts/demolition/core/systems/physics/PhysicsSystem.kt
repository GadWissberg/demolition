package com.gadarts.demolition.core.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.CommonData
import com.gadarts.demolition.core.systems.CommonData.Companion.CHAIN_COLLISION_SHAPE_HEIGHT
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.player.PlayerSystemEventsSubscriber

class PhysicsSystem : GameEntitySystem<PhysicsSystemEventsSubscriber>(),
    PlayerSystemEventsSubscriber {

    private val bulletEngineHandler = BulletEngineHandler()
    override val subscribers: HashSet<PhysicsSystemEventsSubscriber> = HashSet()
    private val constraints = ArrayList<PhysicsConstraint>()

    override fun initialize(am: GameAssetManager) {
        bulletEngineHandler.initialize(commonData, engine)
    }


    override fun resume(delta: Long) {
    }


    override fun update(deltaTime: Float) {
        bulletEngineHandler.update(deltaTime)
    }

    override fun dispose() {
        bulletEngineHandler.dispose()
        constraints.forEach { it.dispose() }
    }

    private fun insertConstraints() {
        constraints.forEach {
            bulletEngineHandler.collisionWorld.addConstraint(
                it,
                it.disableCollisionsBetweenLinkedBodies
            )
        }
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
        addConstraintBetweenChains(chains[4], chains[5])
        addConstraintBetweenChains(chains[5], chains[6])
        addConstraintBetweenChainAndBall(chains[6], ball)
        insertConstraints()
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

    companion object {
        val auxVector1 = Vector3()
        val auxVector2 = Vector3()
        const val CRANE_CONST_REL_POINT_X = CommonData.CRANE_SHAPE_HALF_WIDTH * 2F - 0.1F
        const val CRANE_CONST_REL_POINT_Y = CommonData.CRANE_SHAPE_HALF_WIDTH * 2F + 0.2F
    }
}
