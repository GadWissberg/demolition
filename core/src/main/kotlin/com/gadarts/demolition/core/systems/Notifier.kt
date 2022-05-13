package com.gadarts.demolition.core.systems

interface Notifier<T> where T : SystemEventsSubscriber{
    val subscribers: HashSet<T>


    fun subscribeForEvents(subscriber: T) {
        subscribers.add(subscriber)
    }


}
