package com.atthapon.androidreduxlib.redroid

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

object RxReDroid {
    fun <State> flowable(store: Store<State>): Flowable<Store<State>> {
        return Flowable.create<Store<State>>({ emitter ->
            val subscription = store.subscribe {
                emitter.onNext(store)
            }

            var isDisposed = false
            emitter.setDisposable(object: Disposable {
                override fun isDisposed(): Boolean = isDisposed

                override fun dispose() {
                    if (isDisposed) return
                    isDisposed = true
                    subscription.invoke()
                }

            })
        }, BackpressureStrategy.MISSING)
    }
}