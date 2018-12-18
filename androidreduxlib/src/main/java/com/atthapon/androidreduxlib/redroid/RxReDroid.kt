package com.atthapon.androidreduxlib.redroid

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

object RxReDroid {
    @JvmStatic
    fun flowable(store: ReDroidStore): Flowable<ReDroidStore> {
        return Flowable.create<ReDroidStore>({ emitter ->
            val subscription = store.subscribe {
                emitter.onNext(store)
            }

            var isDisposed = false
            emitter.setDisposable(object : Disposable {
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