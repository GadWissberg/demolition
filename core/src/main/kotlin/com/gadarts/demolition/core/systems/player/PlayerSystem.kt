package com.gadarts.demolition.core.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.dynamics.btPoint2PointConstraint
import com.badlogic.gdx.physics.bullet.dynamics.btTypedConstraint
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.assets.ModelsDefinitions
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier
import com.gadarts.demolition.core.systems.input.InputSystemEventsSubscriber
import com.gadarts.demolition.core.systems.map.MapSystem

class PlayerSystem : GameEntitySystem(), Notifier<PlayerSystemEventsSubscriber>,
    InputSystemEventsSubscriber, InputProcessor {


    private lateinit var crane: Entity
    private lateinit var player: Entity
    private val prevTouchPos = Vector2()
    override val subscribers: HashSet<PlayerSystemEventsSubscriber> = HashSet()
    private val constraints = ArrayList<btTypedConstraint>()
    override fun initialize(am: GameAssetManager) {
        addPlayer()
    }

    private fun addPlayer() {
        addPlayerWheels()
        addPlayerBody()
        addCrane()
    }

    private fun addCrane() {
        crane = EntityBuilder.begin()
            .addModelInstanceComponent(
                assetsManager.getAssetByDefinition(ModelsDefinitions.CRANE),
                auxVector1.set(-CRANE_OFFSET_X, CRANE_OFFSET_Y, 0F)
            )
            .addPhysicsComponent(
                btBoxShape(
                    auxVector1.set(
                        CRANE_SHAPE_WIDTH,
                        CRANE_SHAPE_HEIGHT,
                        CRANE_SHAPE_DEPTH
                    )
                ), Matrix4()
            )
            .finishAndAddToEngine()
        placeCraneCollisionShape()
    }

    private fun placeCraneCollisionShape() {
        ComponentsMapper.physics.get(crane).rigidBody.motionState.getWorldTransform(auxMatrix)
        auxMatrix.rotate(Vector3.Z, CRANE_COL_SHAPE_INITIAL_ANGLE).translate(
            CRANE_COL_SHAPE_INITIAL_POS_X,
            CRANE_COL_SHAPE_INITIAL_POS_Y,
            CRANE_COL_SHAPE_INITIAL_POS_Z
        )
        ComponentsMapper.physics.get(crane).rigidBody.motionState.setWorldTransform(auxMatrix)
    }

    private fun addPlayerBody() {
        player = EntityBuilder.begin().addModelInstanceComponent(
            assetsManager.getAssetByDefinition(ModelsDefinitions.BODY),
            MapSystem.auxVector1.set(0F, BODY_OFFSET_Y, 0F)
        ).finishAndAddToEngine()
    }

    private fun addPlayerWheels() {
        EntityBuilder.begin().addModelInstanceComponent(
            assetsManager.getAssetByDefinition(ModelsDefinitions.CRANE_WHEELS),
            MapSystem.auxVector1.set(0F, WHEELS_OFFSET_Y, 0F)
        ).finishAndAddToEngine()
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
    }

    override fun onInputInitialized() {
        if (DefaultGameSettings.DEBUG_INPUT) return
        (Gdx.input.inputProcessor as InputMultiplexer).addProcessor(this)
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        prevTouchPos.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val playerModelIns = ComponentsMapper.modelInstance.get(player).modelInstance
        playerModelIns.transform.rotate(Vector3.Y, (screenX - prevTouchPos.x) * ROTATION_SCALE)
        val craneModelIns = ComponentsMapper.modelInstance.get(crane).modelInstance
        craneModelIns.transform.translate(CRANE_OFFSET_X, 0F, 0F)
        craneModelIns.transform.rotate(Vector3.Y, (screenX - prevTouchPos.x) * ROTATION_SCALE)
        craneModelIns.transform.translate(-CRANE_OFFSET_X, 0F, 0F)
        prevTouchPos.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

//    private fun addChains(modelBuilder: ModelBuilder): Entity {
//        val ball = addBall(modelBuilder)
//        val chain1 = addChain(auxVector1.set(0F, 2F, 0F))
//        val chain2 = addChain(auxVector1.set(0F, 2.25F, 0F))
//        val chain3 = addChain(auxVector1.set(0F, 2.5F, 0F))
//        val chain4 = addChain(auxVector1.set(0F, 2.5F, 0F))
//        val chain5 = addChain(auxVector1.set(0F, 2.5F, 0F))
//        constraints.add(
//            btPoint2PointConstraint(
//                ComponentsMapper.physics.get(ball).rigidBody,
//                ComponentsMapper.physics.get(chain1).rigidBody,
//                MapSystem.auxVector1.set(0F, 0.5F, 0F), MapSystem.auxVector2.set(0F, -0.25F, 0F)
//            )
//        )
//        addChainToChainConstraint(chain1, chain2)
//        addChainToChainConstraint(chain2, chain3)
//        addChainToChainConstraint(chain3, chain4)
//        addChainToChainConstraint(chain4, chain5)
//        return chain5
//    }

    private fun addChainToChainConstraint(
        chain_1: Entity,
        chain_2: Entity
    ) {
        constraints.add(
            btPoint2PointConstraint(
                ComponentsMapper.physics.get(chain_1).rigidBody,
                ComponentsMapper.physics.get(chain_2).rigidBody,
                MapSystem.auxVector1.set(0F, 0.25F, 0F), MapSystem.auxVector2.set(0F, -0.25F, 0F)
            )
        )
    }

//    private fun addChain(position: Vector3): Entity {
//        val chainModelInstance = assetsManager.getAssetByDefinition(ModelsDefinitions.STRING)
//        val collisionShape = btCapsuleShape(0.1F, 0.25F)
//        return EntityBuilder.begin()
//            .addModelInstanceComponent(chainModelInstance, position)
//            .addPhysicsComponent(
//                collisionShape,
//                chainModelInstance.transform,
//                1F,
//                btBroadphaseProxy.CollisionFilterGroups.CharacterFilter
//            )
//            .finishAndAddToEngine()
//    }

    companion object {
        const val ROTATION_SCALE = 0.1F
        val auxVector1 = Vector3()
        val auxVector2 = Vector3()
        val auxMatrix = Matrix4()
        const val WHEELS_OFFSET_Y = 0.04F
        const val BODY_OFFSET_Y = 0.25F
        const val CRANE_OFFSET_X = 0.6F
        const val CRANE_OFFSET_Y = 1F
        const val CRANE_SHAPE_WIDTH = 1F
        const val CRANE_SHAPE_HEIGHT = 0.1F
        const val CRANE_SHAPE_DEPTH = 0.12F
        const val CRANE_COL_SHAPE_INITIAL_ANGLE = 8F
        const val CRANE_COL_SHAPE_INITIAL_POS_X = 0.6F
        const val CRANE_COL_SHAPE_INITIAL_POS_Y = 2.9F
        const val CRANE_COL_SHAPE_INITIAL_POS_Z = 0F
    }
}
