package com.gadarts.demolition.core.systems.map

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier

class MapSystem : GameEntitySystem(), Notifier<MapSystemEventsSubscriber> {

    private var direction = Vector3()
    private var lastChange: Long = 0
    private lateinit var holder: Entity
    private lateinit var holderModel: Model
    private lateinit var chainModel: Model
    private lateinit var ballModel: Model
    private lateinit var groundModel: Model
    override val subscribers: HashSet<MapSystemEventsSubscriber> = HashSet()
    override fun initialize(am: GameAssetManager) {
        val modelBuilder = ModelBuilder()
        addGround(modelBuilder)
    }


    private fun createChainModel(modelBuilder: ModelBuilder) {
        chainModel = modelBuilder.createCapsule(
            0.1F, 0.5F, 20,
            Material(ColorAttribute.createDiffuse(Color.CORAL)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    private fun addGround(modelBuilder: ModelBuilder) {
        createGroundModel(modelBuilder)
        val collisionShape = btBoxShape(auxVector1.set(GROUND_SIZE / 2F, 0.01f, GROUND_SIZE / 2F))
        EntityBuilder.begin()
            .addModelInstanceComponent(groundModel, Vector3.Zero)
            .addPhysicsComponent(collisionShape)
            .finishAndAddToEngine()
    }

//    private fun createHolder(modelBuilder: ModelBuilder, chain5: Entity) {
//        holderModel = modelBuilder.createBox(
//            1F, 0.5F, 1F,
//            Material(ColorAttribute.createDiffuse(Color.BROWN)),
//            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
//        )
//        val modelInstance = ModelInstance(holderModel)
//        val collisionShape = btBoxShape(auxVector1.set(0.5F, 0.25F, 0.5F))
//        holder = EntityBuilder.begin()
//            .addModelInstanceComponent(modelInstance, auxVector1.set(0F, 5F, 0F))
//            .addPhysicsComponent(collisionShape, modelInstance.transform)
//            .finishAndAddToEngine()
//        constraints.add(
//            btPoint2PointConstraint(
//                ComponentsMapper.physics.get(chain5).rigidBody,
//                ComponentsMapper.physics.get(holder).rigidBody,
//                auxVector1.set(0F, 0.25F, 0F), auxVector2.set(0F, -0.25F, 0F)
//            )
//        )
//    }

    private fun addBall(modelBuilder: ModelBuilder): Entity {
        createBallModel(modelBuilder)
        val ballModelInstance = ModelInstance(ballModel)
        val collisionShape = btSphereShape(0.5F)
        return EntityBuilder.begin()
            .addModelInstanceComponent(ballModelInstance, auxVector1.set(0F, 1F, 0F))
            .addPhysicsComponent(
                collisionShape,
                ballModelInstance.transform,
                2F,
                btBroadphaseProxy.CollisionFilterGroups.CharacterFilter
            )
            .finishAndAddToEngine()
    }

    private fun createBallModel(modelBuilder: ModelBuilder) {
        ballModel = modelBuilder.createSphere(
            1F, 1F, 1F, 10, 10,
            Material(ColorAttribute.createDiffuse(Color.BLUE)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    override fun update(deltaTime: Float) {
//        if (TimeUtils.timeSinceMillis(lastChange) > 2000F) {
//            lastChange = TimeUtils.millis()
//            direction.setToRandomDirection()
//        }
//        val motionState = ComponentsMapper.physics.get(holder).rigidBody.motionState
//        val modelInstance = ComponentsMapper.modelInstance.get(holder).modelInstance
//        motionState.setWorldTransform(
//            auxMatrix.set(modelInstance.transform)
//                .translate(auxVector_1.set(direction).scl(deltaTime))
//        )
    }

    private fun createGroundModel(modelBuilder: ModelBuilder) {
        groundModel = modelBuilder.createBox(
            GROUND_SIZE, 0.1F, GROUND_SIZE,
            Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
        groundModel.dispose()
        ballModel.dispose()
        chainModel.dispose()
        holderModel.dispose()
    }

    companion object {
        private const val GROUND_SIZE = 10F
        private val auxVector1 = Vector3()
        private val auxVector2 = Vector3()
        private val auxMatrix = Matrix4()
    }
}