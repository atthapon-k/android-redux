package com.atthapon.androidredux

import com.atthapon.androidreduxlib.redroid.Action

/**
 * Created by Atthapon Korkaew on 18/12/2018 AD.
 * Copyright © 2018 Onedaycat. All rights reserved.
 */
sealed class UserAction {
    data class SetUser(val users: ArrayList<User>) : Action
}