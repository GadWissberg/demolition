package com.gadarts.demolition.core.components

class CharacterComponent : GameComponent() {
    private var hp: Int = 0

    override fun reset() {
    }

    fun init(hp: Int) {
        this.hp = hp
    }

}
