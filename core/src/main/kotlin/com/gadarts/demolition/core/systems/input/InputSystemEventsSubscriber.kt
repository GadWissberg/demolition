package com.gadarts.demolition.core.systems.input

import com.gadarts.demolition.core.systems.SystemEventsSubscriber

interface InputSystemEventsSubscriber : SystemEventsSubscriber {
    fun onInputInitialized()

}
