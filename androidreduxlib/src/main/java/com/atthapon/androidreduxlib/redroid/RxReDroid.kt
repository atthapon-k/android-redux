package com.atthapon.androidreduxlib.redroid

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

object RxReDroid {
    @JvmStatic
    fun <State> flowable(store: ReDroidStore<State>): Flowable<ReDroidStore<State>> {
        return Flowable.create<ReDroidStore<State>>({ emitter ->
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