package com.example.frontend.controller.infrastructure

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object ApplicationDelegates {
    fun <T> setOnce(defaultValue: T? = null): ReadWriteProperty<Any?, T> = SetOnce(defaultValue)

    private class SetOnce<T>(defaultValue: T? = null) : ReadWriteProperty<Any?, T> {
        private var isSet = false
        private var value: T? = defaultValue

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value ?: throw IllegalStateException("${property.name} not initialized")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = synchronized(this) {
            if (!isSet) {
                this.value = value
                isSet = true
            }
        }
    }
}

//garantisce che una proprietà venga inizializzata una sola volta e poi venga mantenuta immutabile o che l'inizializzazione venga eseguita in modo controllato
