package com.atthapon.redruxlib.redrux

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

object RxRedrux {
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