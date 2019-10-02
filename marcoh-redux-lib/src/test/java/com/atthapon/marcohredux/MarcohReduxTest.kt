package com.atthapon.marcohredux

import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by Atthapon Korkaew on 2019-10-02.
 */

data class CounterState(val counter: Int = 0) : State

sealed class CounterAction : Action {
    data class Increment(val by: Int) : CounterAction()
    data class Decrement(val by: Int) : CounterAction()
}

class MarcohReduxTest {
    private val counterReducer = object : Reducer<CounterState> {
        override fun reduce(currentState: CounterState, action: Action): CounterState {
            val counter = currentState.counter
            return when (action) {
                is CounterAction.Increment -> currentState.copy(counter = counter.plus(action.by))
                is CounterAction.Decrement -> currentState.copy(counter = counter.minus(action.by))
                else -> currentState
            }
        }
    }

    @Test
    fun `store dispatch increase action ensure counter increased`() {
        val store = Store(CounterState(), counterReducer, Schedulers.trampoline())
        val test = store.states.test()

        store.dispatch(CounterAction.Increment(3))

        test.assertValuesOnly(
            CounterState(0) to Store.NoAction,
            CounterState(3) to CounterAction.Increment(3)
        )

        store.dispatch(CounterAction.Increment(10))

        test.assertValuesOnly(
            CounterState(0) to Store.NoAction,
            CounterState(3) to CounterAction.Increment(3),
            CounterState(13) to CounterAction.Increment(10)
        )
    }

    @Test
    fun `store dispatch decrease action ensure counter decrease`() {
        val store = Store(CounterState(), counterReducer, Schedulers.trampoline())
        val test = store.states.test()

        store.dispatch(CounterAction.Decrement(3))

        test.assertValuesOnly(
            CounterState(0) to Store.NoAction,
            CounterState(-3) to CounterAction.Decrement(3)
        )

        store.dispatch(CounterAction.Decrement(10))

        test.assertValuesOnly(
            CounterState(0) to Store.NoAction,
            CounterState(-3) to CounterAction.Decrement(3),
            CounterState(-13) to CounterAction.Decrement(10)
        )
    }
}