package com.gadarts.demolition.core.systems.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.CommonData.Companion.MAP_SIZE
import com.gadarts.demolition.core.systems.GameEntitySystem

/**
 * Responsible for the environment.
 */
class MapSystem : GameEntitySystem<MapSystemEventsSubscriber>() {

    private lateinit var gridModel: Model
    private lateinit var groundModel: Model
    override val subscribers: HashSet<MapSystemEventsSubscriber> = HashSet()

    override fun initialize(am: GameAssetManager) {
        val modelBuilder = ModelBuilder()
        addBoundaries(modelBuilder)
        addGrid(modelBuilder)
        createDirectionsMap()
    }

    private fun createDirectionsMap() {
        commonData.directionsMapping = Array(MAP_SIZE.toInt()) {
            Array(MAP_SIZE.toInt()) {
                Vector3(1F, 0F, 0F)
            }
        }
    }

    private fun addGrid(modelBuilder: ModelBuilder) {
        createGridModel(modelBuilder)
        EntityBuilder.begin()
            .addModelInstanceComponent(
                gridModel,
                auxVector1.set(MAP_SIZE / 2F, 0.1F, MAP_SIZE / 2F)
            )
            .finishAndAddToEngine()
    }

    private fun createGridModel(modelBuilder: ModelBuilder) {
        gridModel = modelBuilder.createLineGrid(
            MAP_SIZE.toInt(),
            MAP_SIZE.toInt(),
            1F,
            1F,
            Material(ColorAttribute.createDiffuse(Color.BROWN)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    private fun addBoundaries(modelBuilder: ModelBuilder) {
        createGroundModel(modelBuilder)
        val collisionShape = createBoundariesCollisionShape()
        EntityBuilder.begin()
            .addModelInstanceComponent(
                groundModel,
                auxVector1.set(MAP_SIZE / 2F, 0F, MAP_SIZE / 2F)
            )
            .addPhysicsComponent(collisionShape)
            .finishAndAddToEngine()
    }

    private fun createBoundariesCollisionShape(): btCollisionShape {
        val collisionShape = btCompoundShape()
        val ground = btStaticPlaneShape(auxVector1.set(0F, 1F, 0F), 0F)
        val wallX = btStaticPlaneShape(auxVector1.set(1F, 0F, 0F), 0F)
        val wallNegX = btStaticPlaneShape(auxVector1.set(-1F, 0F, 0F), -MAP_SIZE)
        val wallZ = btStaticPlaneShape(auxVector1.set(0F, 0F, 1F), 0F)
        val wallNegZ = btStaticPlaneShape(auxVector1.set(0F, 0F, -1F), -MAP_SIZE)
        collisionShape.addChildShape(auxMatrix.idt(), ground)
        collisionShape.addChildShape(auxMatrix.idt(), wallX)
        collisionShape.addChildShape(auxMatrix.idt(), wallNegX)
        collisionShape.addChildShape(auxMatrix.idt(), wallZ)
        collisionShape.addChildShape(auxMatrix.idt(), wallNegZ)
        return collisionShape
    }

    private fun createGroundModel(modelBuilder: ModelBuilder) {
        groundModel = modelBuilder.createBox(
            MAP_SIZE, 0.1F, MAP_SIZE,
            Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
        groundModel.dispose()
        gridModel.dispose()
    }

    companion object {
        private val auxVector1 = Vector3()
        private val auxMatrix = Matrix4()
    }
}