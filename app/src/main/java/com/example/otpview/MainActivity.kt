package com.example.otpview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        val tag = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        otpView.requestInput()

        otpView.setOnLastBoxWasFilledListener {
            Log.d(tag, "Reaching the last box")
        }


        btnGetValue.setOnClickListener {
            val output = otpView.getValue()
            tvValue.text = "value: ${output.value} isCompleted: ${output.isCompleted}"
        }

        btnClear.setOnClickListener {
            otpView.clearValue()
            val output = otpView.getValue()
            tvValue.text = "value: ${output.value} isCompleted: ${output.isCompleted}"
        }
    }


}
