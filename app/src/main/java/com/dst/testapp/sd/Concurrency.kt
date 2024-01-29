package com.dst.testapp.sd

import java.util.concurrent.atomic.AtomicReference


class AtomicCounter() {
    fun get(): Int {return 0}
    fun getAndIncrement(): Int {return 0}
    fun set(i: Int) {}
}

class AtomicRef<T>(initialValue: T) {
    private val atomicReference = AtomicReference<T>(initialValue)

    var value: T
        get() = atomicReference.get()
        set(newValue) {
            atomicReference.set(newValue)
        }

    fun compareAndSet(expectedValue: T, newValue: T): Boolean {
        return atomicReference.compareAndSet(expectedValue, newValue)
    }
}


/*
 class AtomicCounter() {
    fun get(): Int {return 0}
     fun getAndIncrement(): Int {return 0}
     fun set(i: Int) {}
 }


import java.util.concurrent.atomic.AtomicReference

class AtomicRef<T>(initialValue: T) {
    private val atomicReference = AtomicReference<T>(initialValue)

    var value: T
        get() = atomicReference.get()
        set(newValue) {
            atomicReference.set(newValue)
        }

    fun compareAndSet(expectedValue: T, newValue: T): Boolean {
        return atomicReference.compareAndSet(expectedValue, newValue)
    }
}
*/
