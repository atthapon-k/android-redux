package com.atthapon.redruxlib.redrux

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Action
typealias Reducer<State> = (State, Action) -> State
typealias Subscription<State> = (State) -> Unit
typealias UnSubscription = () -> Boolean

interface Store<State> {
    var state: State
    fun dispatch(action: Action)
    fun subscribe(subscription: Subscription<State>): UnSubscription
}

interface Middleware<State> {
    fun applyMiddleware(store: Store<State>, action: Action): Action
}

open class StoreImpl<State>(
    initialState: State,
    private val reducers: ArrayList<Reducer<State>>,
    private val middlewares: ArrayList<Middleware<State>>
) : Store<State> {

    private val lock = ReentrantLock()
    private val subscriptions = arrayListOf<Subscription<State>>()

    @Volatile
    override var state: State = initialState

    override fun dispatch(action: Action) {
        lock.withLock {
            val newAction = applyMiddleware(action)
            state = applyReducers(state, newAction)
        }
        subscriptions.forEach { it(state) }
    }

    override fun subscribe(subscription: Subscription<State>): () -> Boolean {
        subscriptions.add(subscription)
        subscription(state)
        return { subscriptions.remove(subscription) }
    }

    private fun applyReducers(state: State, action: Action): State {
        var newState = state
        for (reducer in reducers) {
            newState = reducer(newState, action)
        }
        return newState
    }

    private fun applyMiddleware(action: Action): Action {
        var newAction = action
        for (middleware in middlewares) {
            newAction = middleware.applyMiddleware(this, action)
        }
        return newAction
    }
}
