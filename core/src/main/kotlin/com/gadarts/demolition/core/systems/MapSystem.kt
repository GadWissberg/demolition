package com.gadarts.demolition.core.systems

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.gadarts.demolition.core.EntityBuilder
import com.gadarts.demolition.core.assets.GameAssetManager


class MapSystem : GameEntitySystem() {
    private lateinit var ballModel: Model
    private lateinit var groundModel: Model

    override fun initialize(am: GameAssetManager) {
        val modelBuilder = ModelBuilder()
        createGroundModel(modelBuilder)
//        val collisionShape = btBoxShape(auxVector.set(GROUND_SIZE, 0.01f, GROUND_SIZE))
        EntityBuilder.begin()
            .addModelInstanceComponent(groundModel, Vector3.Zero)
//            .addPhysicsComponent(collisionShape)
            .finishAndAddToEngine()
        addBall(modelBuilder)
    }

    private fun addBall(modelBuilder: ModelBuilder) {
        ballModel = modelBuilder.createSphere(
            1F, 1F, 1F, 10, 10,
            Material(ColorAttribute.createDiffuse(Color.BLUE)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
//        val collisionShape = btSphereShape(1F)
        EntityBuilder.begin()
//            .addPhysicsComponent(collisionShape)
            .addModelInstanceComponent(ballModel,auxVector.set(0F, 2F, 0F)
        ).finishAndAddToEngine()
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
    }

    companion object {
        const val GROUND_SIZE = 10F
        val auxVector = Vector3()
    }
}
