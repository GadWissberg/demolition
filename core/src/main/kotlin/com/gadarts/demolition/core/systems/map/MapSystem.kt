package com.gadarts.demolition.core.systems.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier

/**
 * Responsible for the environment.
 */
class MapSystem : GameEntitySystem(), Notifier<MapSystemEventsSubscriber> {

    private lateinit var groundModel: Model
    override val subscribers: HashSet<MapSystemEventsSubscriber> = HashSet()

    override fun initialize(am: GameAssetManager) {
        val modelBuilder = ModelBuilder()
        addGround(modelBuilder)
    }

    private fun addGround(modelBuilder: ModelBuilder) {
        createGroundModel(modelBuilder)
        val collisionShape = btBoxShape(auxVector1.set(GROUND_SIZE / 2F, 0.01f, GROUND_SIZE / 2F))
        EntityBuilder.begin()
            .addModelInstanceComponent(groundModel, Vector3.Zero)
            .addPhysicsComponent(collisionShape)
            .finishAndAddToEngine()
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
    }

    companion object {
        private const val GROUND_SIZE = 10F
        private val auxVector1 = Vector3()
    }
}