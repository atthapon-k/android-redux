package com.atthapon.androidredux

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atthapon.androidreduxlib.redroid.StoreImpl

class MainActivity : AppCompatActivity() {

    private val store = StoreImpl(MainState(), arrayListOf(userReducer), arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val users = ArrayList<User>().apply {
            this.add(User("1"))
        }

        store.dispatch(UserAction.SetUser(users))

        val s = store.state.userState.users
    }
}
