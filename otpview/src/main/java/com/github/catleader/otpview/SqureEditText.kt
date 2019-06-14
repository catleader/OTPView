package com.github.catleader.otpview

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class SquareEditText : EditText {

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setSelectAllOnFocus(true)
        inputType = InputType.TYPE_CLASS_NUMBER
        maxLines = 1
        filters = arrayOf(InputFilter.LengthFilter(1))
        gravity = Gravity.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val resolvedWidth = View.resolveSize(widthSize, widthMeasureSpec)
        val resolvedHeight = View.resolveSize(widthSize, heightMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    fun setSquareEditTextListeners(
        previousEdt: EditText?,
        nextEdt: EditText?
    ) {
        setOnClickListener { selectAll() }
        addTextChangedListener(AutoRequestFocusNextEditTextListener(nextEdt))
        setOnKeyListener { _, keyCode, event ->
            if (length() == 0 && keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                previousEdt?.requestFocus()
                previousEdt?.setSelection(previousEdt.length())
                previousEdt?.selectAll()
            }
            false
        }
    }

    var onLastBoxWasFilled: (() -> Unit)? = null

    inner class AutoRequestFocusNextEditTextListener(private val nextEdt: EditText?) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            val length = s?.length
            if (length == 1) {
                if (nextEdt != null) {
                    nextEdt.requestFocus()
                    nextEdt.setSelection(nextEdt.length())
                    nextEdt.selectAll()
                } else {
                    onLastBoxWasFilled?.invoke()
                    hideKeyboard()
                    (parent as? ViewGroup?)?.requestFocus()
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}