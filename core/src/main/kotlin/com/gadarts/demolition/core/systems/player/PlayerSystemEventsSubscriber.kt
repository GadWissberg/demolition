package com.gadarts.demolition.core.systems.player

import com.badlogic.ashley.core.Entity
import com.gadarts.demolition.core.systems.SystemEventsSubscriber

interface PlayerSystemEventsSubscriber : SystemEventsSubscriber {
    fun onPlayerAdded(chains: List<Entity>, crane: Entity, ball: Entity)

}
