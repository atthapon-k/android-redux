package com.atthapon.androidreduxlib.redroid

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
    fun applyMiddleware(store: Store<State>, action: Action, next: Next<State>): Action
}

interface Next<State> {
    fun next(store: Store<State>, action: Action): Action
}

class NextMiddleware<State>(val middleware: Middleware<State>, val next: Next<State>): Next<State> {
    override fun next(store: Store<State>, action: Action): Action {
        return middleware.applyMiddleware(store, action, next)
    }
}

class EndOfChain<State>: Next<State> {
    override fun next(store: Store<State>, action: Action): Action = action
}

class StoreImpl<State>(
    initialState: State,
    private val reducers: ArrayList<Reducer<State>>,
    private val middlewares: ArrayList<Middleware<State>>
): Store<State> {

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
        val chain = createNext(0)
        return chain.next(this, action)
    }

    private fun createNext(index: Int): Next<State> {
        if (index == middlewares.size) {
            return EndOfChain()
        }
        return NextMiddleware(middlewares[index], createNext(index + 1))
    }
}
