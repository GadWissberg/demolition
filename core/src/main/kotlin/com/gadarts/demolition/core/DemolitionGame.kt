package com.gadarts.demolition.core

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.gadarts.demolition.core.assets.GameAssetManager
import com.gadarts.demolition.core.screens.GamePlayScreen


class DemolitionGame(private val androidInterface: com.gadarts.demolition.core.AndroidInterface) :
    Game() {

    private lateinit var assetsManager: GameAssetManager

    override fun create() {
        loadAssets()
        Gdx.input.inputProcessor = InputMultiplexer()
        setScreen(GamePlayScreen(assetsManager))
    }

    private fun loadAssets() {
        assetsManager = GameAssetManager()
        assetsManager.loadAssets()
        assetsManager.finishLoading()
    }

}