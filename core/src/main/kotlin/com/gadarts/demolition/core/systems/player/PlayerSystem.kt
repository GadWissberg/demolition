package com.gadarts.demolition.core.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.*
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.assets.ModelsDefinitions
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.CommonData.Companion.CHAIN_COLLISION_SHAPE_HEIGHT
import com.gadarts.demolition.core.systems.CommonData.Companion.CHAIN_COLLISION_SHAPE_RADIUS
import com.gadarts.demolition.core.systems.CommonData.Companion.CRANE_SHAPE_DEPTH
import com.gadarts.demolition.core.systems.CommonData.Companion.CRANE_SHAPE_HEIGHT
import com.gadarts.demolition.core.systems.CommonData.Companion.CRANE_SHAPE_WIDTH
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier
import com.gadarts.demolition.core.systems.input.InputSystemEventsSubscriber

class PlayerSystem : GameEntitySystem(), Notifier<PlayerSystemEventsSubscriber>,
    InputSystemEventsSubscriber, InputProcessor {


    private val craneRotation = Vector3()
    private lateinit var crane: Entity
    private lateinit var player: Entity
    private val prevTouchPos = Vector2()
    override val subscribers: HashSet<PlayerSystemEventsSubscriber> = HashSet()

    override fun initialize(am: GameAssetManager) {
        addPlayer()
    }

    private fun addPlayer() {
        addPlayerWheels()
        addPlayerBody()
        addCrane()
        addChainsAndBall()
    }

    private fun addChainsAndBall() {
        val chains = addChains()
        val ball = addBall()
        subscribers.forEach {
            it.onPlayerAdded(
                chains,
                crane,
                ball
            )
        }
    }

    private fun addChains(): List<Entity> {
        val chain1 = addChain(auxVector1.set(0F, 1F, 0F))
        val chain2 = addChain(auxVector1.set(0F, 1F, 0F))
        val chain3 = addChain(auxVector1.set(0F, 1F, 0F))
        val chain4 = addChain(auxVector1.set(0F, 1F, 0F))
        val chain5 = addChain(auxVector1.set(0F, 1F, 0F))
        return listOf(chain1, chain2, chain3, chain4, chain5)
    }

    private fun addBall(): Entity {
        val colShape = btSphereShape(BALL_RADIUS)
        val model = assetsManager.getAssetByDefinition(ModelsDefinitions.BALL)
        val modelInstance = ModelInstance(model)
        val ball = EntityBuilder.begin().addModelInstanceComponent(
            modelInstance,
            auxVector1.set(0F, 1F, 0F)
        ).addPhysicsComponent(
            colShape,
            modelInstance.transform,
            80F,
            CF_CHARACTER_OBJECT
        ).finishAndAddToEngine()
        val rigidBody = ComponentsMapper.physics.get(ball).rigidBody
        rigidBody.setDamping(0F, BALL_DAMPING)
        val motionState = rigidBody.motionState
        motionState.getWorldTransform(auxMatrix1)
        motionState.setWorldTransform(auxMatrix1.setToTranslation(0F, 1F, 0F))
        return ball
    }

    private fun addChain(position: Vector3): Entity {
        val colShape = btCapsuleShape(CHAIN_COLLISION_SHAPE_RADIUS, CHAIN_COLLISION_SHAPE_HEIGHT)
        val model = assetsManager.getAssetByDefinition(ModelsDefinitions.STRING)
        val modelInstance = ModelInstance(model)
        val chain = EntityBuilder.begin().addModelInstanceComponent(
            modelInstance,
            position
        ).addPhysicsComponent(
            colShape,
            modelInstance.transform,
            10F,
            CF_CHARACTER_OBJECT
        ).finishAndAddToEngine()
        ComponentsMapper.physics.get(chain).rigidBody.setDamping(0F, 5F)
        return chain
    }

    private fun addCrane() {
        crane = EntityBuilder.begin()
            .addModelInstanceComponent(
                assetsManager.getAssetByDefinition(ModelsDefinitions.CRANE),
                auxVector1.set(-CRANE_MODEL_OFFSET_X, CRANE_MODEL_OFFSET_Y, 0F)
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
        val motionState = ComponentsMapper.physics.get(crane).rigidBody.motionState
        motionState.getWorldTransform(auxMatrix1)
        auxMatrix1.translate(
            CRANE_COL_SHAPE_INITIAL_POS_X,
            CRANE_COL_SHAPE_INITIAL_POS_Y,
            CRANE_COL_SHAPE_INITIAL_POS_Z
        )
        motionState.setWorldTransform(auxMatrix1)
    }

    private fun addPlayerBody() {
        player = EntityBuilder.begin().addModelInstanceComponent(
            assetsManager.getAssetByDefinition(ModelsDefinitions.BODY),
            auxVector1.set(0F, BODY_OFFSET_Y, 0F)
        ).finishAndAddToEngine()
    }

    private fun addPlayerWheels() {
        EntityBuilder.begin().addModelInstanceComponent(
            assetsManager.getAssetByDefinition(ModelsDefinitions.CRANE_WHEELS),
            auxVector1.set(0F, WHEELS_OFFSET_Y, 0F)
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
        if (DefaultGameSettings.DEBUG_INPUT) return false
        handleRotationAroundY(screenX)
        handleRotationAroundZ(screenY)
        prevTouchPos.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    private fun handleRotationAroundZ(screenY: Int) {
        val craneModelIns = ComponentsMapper.modelInstance.get(crane).modelInstance
        val newZ = craneRotation.z + (prevTouchPos.y - screenY) * ROTATION_SCALE
        craneRotation.set(
            craneRotation.x,
            craneRotation.y,
            MathUtils.clamp(newZ, -20F, 20F)
        )
        craneModelIns.transform.getRotation(auxQuat)
        craneModelIns.transform.setFromEulerAngles(
            craneRotation.x,
            auxQuat.pitch,
            craneRotation.z
        )
        craneModelIns.transform.trn(-CRANE_MODEL_OFFSET_X * MathUtils.cosDeg(craneRotation.x), CRANE_MODEL_OFFSET_Y, CRANE_MODEL_OFFSET_X * MathUtils.sinDeg(craneRotation.x))
    }

    private fun handleRotationAroundY(screenX: Int) {
        val motionState = ComponentsMapper.physics.get(crane).rigidBody.motionState
        motionState.getWorldTransform(auxMatrix1)
        val newX = craneRotation.x + (screenX - prevTouchPos.x) * ROTATION_SCALE
        Gdx.app.log("!","{$newX}")
        craneRotation.set(
            MathUtils.clamp(newX, -90F, 90F),
            craneRotation.y,
            craneRotation.z
        )
        rotatePlayerModel(screenX)
        motionState.setWorldTransform(auxMatrix1)
    }

    private fun rotatePlayerModel(screenX: Int) {
        val playerModelIns = ComponentsMapper.modelInstance.get(player).modelInstance
        playerModelIns.transform.getTranslation(auxVector1)
        playerModelIns.transform.setToRotation(Vector3.Y, craneRotation.x)
        playerModelIns.transform.setTranslation(auxVector1)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    companion object {
        private const val ROTATION_SCALE = 0.1F
        private val auxVector1 = Vector3()
        private val auxMatrix1 = Matrix4()
        private val auxQuat = Quaternion()
        private const val WHEELS_OFFSET_Y = 0.04F
        private const val BODY_OFFSET_Y = 0.25F
        private const val CRANE_MODEL_OFFSET_X = 0.6F
        private const val CRANE_MODEL_OFFSET_Y = 1F
        private const val CRANE_COL_SHAPE_INITIAL_POS_X = 0.2F
        private const val CRANE_COL_SHAPE_INITIAL_POS_Y = 3.05F
        private const val CRANE_COL_SHAPE_INITIAL_POS_Z = 0F
        private const val BALL_DAMPING = 0.6F
        private const val BALL_RADIUS = 0.2F

    }
}
