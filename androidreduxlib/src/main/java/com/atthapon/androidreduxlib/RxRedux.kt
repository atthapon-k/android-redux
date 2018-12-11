package com.atthapon.androidreduxlib

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

object RxRedux {
    @JvmStatic
    fun <State, Action> flowable(store: Store<State, Action>): Flowable<Store<State, Action>> {
        return Flowable.create<Store<State, Action>>({ emitter ->
                                                         val subscription = store.subscribe {
                                                             emitter.onNext(store)
                                                         }

                                                         var isDisposed = false
                                                         emitter.setDisposable(object: Disposable {
                                                             override fun isDisposed(): Boolean = isDisposed

                                                             override fun dispose() {
                                                                 if (isDisposed) return
                                                                 isDisposed = true
                                                                 subscription.unsubscribe()
                                                             }

                                                         })
                                                     }, BackpressureStrategy.MISSING)
    }
}