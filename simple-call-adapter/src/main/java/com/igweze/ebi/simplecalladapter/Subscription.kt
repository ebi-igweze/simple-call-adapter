package com.igweze.ebi.simplecalladapter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.Lifecycle.Event.ON_PAUSE
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY

class Subscription {

    private var disposed = false

    fun isDisposed() = disposed

    fun dispose() {
        disposed = true
    }

    fun bind(owner: LifecycleOwner) = bind(owner, ON_DESTROY)

    fun bind(owner: LifecycleOwner, event: Lifecycle.Event) {

        owner.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(ON_PAUSE)
            fun onPause() {
                if (event == ON_PAUSE) {
                    removeObserverAndDispose(owner)
                }
            }

            @OnLifecycleEvent(ON_STOP)
            fun onStop(owner: LifecycleOwner) {
                if (event == ON_STOP) {
                    removeObserverAndDispose(owner)
                }
            }

            @OnLifecycleEvent(ON_DESTROY)
            fun onDestroy(owner: LifecycleOwner) {
                if (event == ON_DESTROY) {
                    removeObserverAndDispose(owner)
                }
            }

            fun removeObserverAndDispose(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                dispose()
            }
        })
    }

}