package com.gadarts.demolition.core.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Disposable
import com.gadarts.demolition.core.assets.GameAssetManager

abstract class GameEntitySystem : Disposable, EntitySystem(), SystemEventsSubscriber {
    lateinit var assetsManager: GameAssetManager
    lateinit var commonData: CommonData

    abstract fun initialize(am: GameAssetManager)
    abstract fun resume(delta: Long)
}
