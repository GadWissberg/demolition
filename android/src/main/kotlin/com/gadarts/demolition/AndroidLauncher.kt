package com.gadarts.demolition

import android.os.Bundle
import android.widget.Toast
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.gadarts.demolition.core.DemolitionGame

class AndroidLauncher : AndroidApplication(), com.gadarts.demolition.core.AndroidInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.numSamples = 2
        initialize(DemolitionGame(this), config)
    }

    override fun toast(msg: String) {
        runOnUiThread {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}