package com.atthapon.androidredux

import com.atthapon.androidreduxlib.redroid.Action

/**
 * Created by Atthapon Korkaew on 18/12/2018 AD.
 * Copyright © 2018 Onedaycat. All rights reserved.
 */

val userReducer = { state: MainState, action: Action ->
    when (action) {
        is UserAction.SetUser -> {
            state.userState.copy(users = action.users)
        }
    }
    state
}