package com.gadarts.demolition.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.audio.Sound

enum class SfxDefinitions(fileNames: Int = 1) : AssetDefinition<Sound> {

    PROPELLER,
    MACHINE_GUN,
    MISSILE,
    AMB_WIND(2),
    AMB_EAGLE,
    AMB_OUD(3),
    CRASH(2);

    private val paths = ArrayList<String>()

    init {
        initializePaths("sfx/%s.wav", fileNames)
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Sound>? {
        return null
    }

    override fun getClazz(): Class<Sound> {
        return Sound::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }
}