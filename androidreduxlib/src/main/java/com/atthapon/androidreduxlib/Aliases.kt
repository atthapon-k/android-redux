package com.atthapon.androidreduxlib

typealias Reducer<State, Action> = (state: State?, action: Action) -> State?

typealias Listener = () -> Unit

typealias NextMiddleware<Action> = (action: Action) -> Unit

typealias Middleware<State, Action> = (state: State?, action: Action, next: NextMiddleware<Action>) -> Unit