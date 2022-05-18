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
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.assets.ModelsDefinitions.*
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.systems.CommonData.Companion.CHAIN_COLLISION_SHAPE_HEIGHT
import com.gadarts.demolition.core.systems.CommonData.Companion.CHAIN_COLLISION_SHAPE_RADIUS
import com.gadarts.demolition.core.systems.CommonData.Companion.CRANE_SHAPE_HALF_DEPTH
import com.gadarts.demolition.core.systems.CommonData.Companion.CRANE_SHAPE_HALF_HEIGHT
import com.gadarts.demolition.core.systems.CommonData.Companion.CRANE_SHAPE_HALF_WIDTH
import com.gadarts.demolition.core.systems.CommonData.Companion.MAP_SIZE
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.input.InputSystemEventsSubscriber

class PlayerSystem : GameEntitySystem<PlayerSystemEventsSubscriber>(),
    InputSystemEventsSubscriber, InputProcessor {


    private val craneRot = Vector3()
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
        val chain1 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        val chain2 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        val chain3 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        val chain4 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        val chain5 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        val chain6 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        val chain7 = addChain(auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F))
        return listOf(chain1, chain2, chain3, chain4, chain5, chain6, chain7)
    }

    private fun addBall(): Entity {
        val modelInstance = ModelInstance(assetsManager.getAssetByDefinition(BALL))
        val ball = EntityBuilder.begin().addModelInstanceComponent(
            modelInstance,
            auxVector1.set(MAP_SIZE/2F, 1F, MAP_SIZE/2F)
        ).addPhysicsComponent(
            btSphereShape(BALL_RADIUS),
            modelInstance.transform,
            BALL_MASS,
            CF_CHARACTER_OBJECT
        ).finishAndAddToEngine()
        initializeBall(ball)
        return ball
    }

    private fun initializeBall(ball: Entity) {
        val rigidBody = ComponentsMapper.physics.get(ball).rigidBody
        rigidBody.setDamping(BALL_LINEAR_DAMPING, BALL_ANGULAR_DAMPING)
        val motionState = rigidBody.motionState
        motionState.getWorldTransform(auxMatrix1)
        motionState.setWorldTransform(auxMatrix1.setToTranslation(0F, 1F, 0F))
    }

    private fun addChain(position: Vector3): Entity {
        val modelInstance = ModelInstance(assetsManager.getAssetByDefinition(CHAIN))
        val chain = EntityBuilder.begin().addModelInstanceComponent(
            modelInstance,
            position
        ).addPhysicsComponent(
            btCapsuleShape(CHAIN_COLLISION_SHAPE_RADIUS, CHAIN_COLLISION_SHAPE_HEIGHT),
            modelInstance.transform,
            CHAIN_MASS,
            CF_CHARACTER_OBJECT
        ).finishAndAddToEngine()
        ComponentsMapper.physics.get(chain).rigidBody.setDamping(0F, 10F)
        return chain
    }

    private fun addCrane() {
        val collisionShape = createCraneCollisionShape()
        val modelInstance = ModelInstance(assetsManager.getAssetByDefinition(CRANE))
        crane = EntityBuilder.begin()
            .addModelInstanceComponent(
                modelInstance,
                auxVector1.set(
                    MAP_SIZE/2F - CRANE_MODEL_OFFSET_X,
                    CRANE_MODEL_OFFSET_Y,
                    MAP_SIZE/2F
                )
            )
            .addPhysicsComponent(collisionShape, modelInstance.transform)
            .finishAndAddToEngine()
    }

    private fun createCraneCollisionShape(): btCompoundShape {
        val collisionShape = btCompoundShape()
        auxVector1.set(CRANE_SHAPE_HALF_WIDTH, CRANE_SHAPE_HALF_HEIGHT, CRANE_SHAPE_HALF_DEPTH)
        val firstPart = btBoxShape(auxVector1)
        val secondPart = btBoxShape(auxVector1)
        addCraneChildShapesToCompound(collisionShape, firstPart, secondPart)
        return collisionShape
    }

    private fun addCraneChildShapesToCompound(
        collisionShape: btCompoundShape,
        firstPart: btBoxShape,
        secondPart: btBoxShape
    ) {
        collisionShape.addChildShape(
            auxMatrix1.idt().translate(0F, CRANE_SHAPE_HALF_WIDTH, 0F).rotate(Vector3.Z, 90F),
            firstPart
        )
        collisionShape.addChildShape(
            auxMatrix1.idt().translate(CRANE_SHAPE_HALF_WIDTH, 1.95F, 0F).rotate(Vector3.Z, 8F),
            secondPart
        )
    }

    private fun addPlayerBody() {
        player = EntityBuilder.begin().addModelInstanceComponent(
            assetsManager.getAssetByDefinition(BODY),
            auxVector1.set(MAP_SIZE/2F, BODY_OFFSET_Y, MAP_SIZE/2F)
        ).finishAndAddToEngine()
    }

    private fun addPlayerWheels() {
        EntityBuilder.begin().addModelInstanceComponent(
            assetsManager.getAssetByDefinition(CRANE_WHEELS),
            auxVector1.set(MAP_SIZE/2F, WHEELS_OFFSET_Y, MAP_SIZE/2F)
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
        calculateCraneRotation(screenX, screenY)
        ComponentsMapper.modelInstance.get(crane).modelInstance.transform.trn(
            MAP_SIZE/2F + -CRANE_MODEL_OFFSET_X * MathUtils.cosDeg(craneRot.x),
            CRANE_MODEL_OFFSET_Y,
            MAP_SIZE/2F + CRANE_MODEL_OFFSET_X * MathUtils.sinDeg(craneRot.x)
        )
        prevTouchPos.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    private fun calculateCraneRotation(screenX: Int, screenY: Int) {
        handleRotationAroundY(screenX)
        handleRotationAroundZ(screenY)
    }

    private fun handleRotationAroundZ(screenY: Int) {
        val craneModelIns = ComponentsMapper.modelInstance.get(crane).modelInstance
        val newZ = craneRot.z + calculateRotationDelta(screenY, prevTouchPos.y)
        craneRot.set(
            craneRot.x,
            craneRot.y,
            MathUtils.clamp(newZ, -CRANE_MAX_ANGLE_AROUND_Z, CRANE_MAX_ANGLE_AROUND_Z)
        )
        craneModelIns.transform.setFromEulerAngles(
            craneRot.x,
            craneModelIns.transform.getRotation(auxQuat).pitch,
            craneRot.z
        )
    }

    private fun calculateRotationDelta(screenCoordinate: Int, prevCoordinate: Float): Float {
        return MathUtils.clamp(
            (prevCoordinate - screenCoordinate) * ROTATION_SCALE,
            -MAX_CRANE_ROT_DELTA,
            MAX_CRANE_ROT_DELTA
        )
    }

    private fun handleRotationAroundY(screenX: Int) {
        val motionState = ComponentsMapper.physics.get(crane).rigidBody.motionState
        motionState.getWorldTransform(auxMatrix1)
        val newX = craneRot.x + calculateRotationDelta(-screenX, -prevTouchPos.x)
        craneRot.set(
            MathUtils.clamp(newX, -CRANE_MAX_ANGLE_AROUND_Y, CRANE_MAX_ANGLE_AROUND_Y),
            craneRot.y,
            craneRot.z
        )
        rotatePlayerModel()
        motionState.setWorldTransform(auxMatrix1)
    }

    private fun rotatePlayerModel() {
        val playerModelIns = ComponentsMapper.modelInstance.get(player).modelInstance
        playerModelIns.transform.getTranslation(auxVector1)
        playerModelIns.transform.setToRotation(Vector3.Y, craneRot.x)
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
        private const val BALL_ANGULAR_DAMPING = 1F
        private const val BALL_LINEAR_DAMPING = 0.2F
        private const val BALL_RADIUS = 0.2F
        private const val BALL_MASS = 80F
        private const val CHAIN_MASS = 100F
        private const val CRANE_MAX_ANGLE_AROUND_Z = 45F
        private const val CRANE_MAX_ANGLE_AROUND_Y = 90F
        private const val MAX_CRANE_ROT_DELTA = 1F

    }
}
