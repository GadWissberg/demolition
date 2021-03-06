package com.gadarts.demolition.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import java.util.*

enum class ShaderDefinitions : AssetDefinition<String> {
    ;

    private val paths = ArrayList<String>()

    init {
        initializePaths("shaders/%s.shader")
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<String>? {
        return null
    }

    override fun getClazz(): Class<String> {
        return String::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }
}