package com.gadarts.demolition.core.systems.render

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.DefaultGameSettings
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.components.ComponentsMapper
import com.gadarts.demolition.core.components.GroundComponent
import com.gadarts.demolition.core.components.ModelInstanceComponent
import com.gadarts.demolition.core.systems.GameEntitySystem
import com.gadarts.demolition.core.systems.Notifier
import com.gadarts.demolition.core.systems.physics.CollisionShapesDebugDrawing
import kotlin.math.max

class RenderSystem : GameEntitySystem<RenderSystemEventsSubscriber>(), Disposable {


    private lateinit var modelBatch: ModelBatch
    private lateinit var modelInstanceEntities: ImmutableArray<Entity>
    private var axisModelHandler = AxisModelHandler()
    override val subscribers: HashSet<RenderSystemEventsSubscriber> = HashSet()
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        modelInstanceEntities = engine!!.getEntitiesFor(
            Family.all(ModelInstanceComponent::class.java)
                .exclude(GroundComponent::class.java)
                .get()
        )
        modelBatch = ModelBatch()
    }

    private fun renderCollisionShapes() {
        if (!DefaultGameSettings.SHOW_COLLISION_SHAPES) return
        val debugDrawingMethod: CollisionShapesDebugDrawing? = commonData.debugDrawingMethod
        debugDrawingMethod?.drawCollisionShapes(commonData.camera)
    }

    private fun resetDisplay(@Suppress("SameParameterValue") color: Color) {
        Gdx.gl.glViewport(0, 0, graphics.width, graphics.height)
        val s = if (graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or s)
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1f)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        resetDisplay(backgroundColor)
        renderModels()
        renderCollisionShapes()
    }

    private fun isVisible(entity: Entity): Boolean {
        val modelInsComp = ComponentsMapper.modelInstance[entity]
        val pos: Vector3 = modelInsComp.modelInstance.transform.getTranslation(auxVector3_1)
        val center: Vector3 = pos.add(modelInsComp.getBoundingBox(auxBox).getCenter(auxVector3_2))
        val dims: Vector3 = auxBox.getDimensions(auxVector3_2)
        dims.x = max(dims.x, max(dims.y, dims.z))
        dims.y = max(dims.x, max(dims.y, dims.z))
        dims.z = max(dims.x, max(dims.y, dims.z))
        return commonData.camera.frustum.boundsInFrustum(center, dims)
    }

    private fun renderModels() {
        modelBatch.begin(commonData.camera)
        axisModelHandler.render(modelBatch)
        for (entity in modelInstanceEntities) {
            renderModel(entity)
        }
        modelBatch.end()
    }

    private fun renderModel(entity: Entity) {
        if (isVisible(entity)) {
            val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
            modelBatch.render(modelInstance)
        }
    }

    override fun initialize(am: GameAssetManager) {
    }

    override fun resume(delta: Long) {

    }

    override fun dispose() {
        modelBatch.dispose()
    }

    companion object {
        val auxVector3_1 = Vector3()
        val auxVector3_2 = Vector3()
        val auxBox = BoundingBox()
        val backgroundColor: Color = Color.WHITE
    }

}
