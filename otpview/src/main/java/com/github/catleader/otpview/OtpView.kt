package com.github.catleader.otpview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources

/**
 * A reusable view for handle OTP input
 */
class OtpView : LinearLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }


    private val editTexts = mutableListOf<SquareEditText>()

    /**
     * Number of OTP boxes.
     */
    var digitCount = resources.getInteger(R.integer.default_digit_count)

    /**
     * Digit box background drawable resource.
     */
    var digitBackgroundRes = R.drawable.default_digit_background


    /**
     * A space between each box in pixels.
     */
    var digitSpaceBetween = resources.getDimensionPixelSize(R.dimen.default_digit_space_between)


    /**
     * Digit font size
     */
    var digitFontSize = resources.getDimension(R.dimen.default_digit_font_size)

    /**
     * Digit font
     */
    var digitFont = "kanit-regular.ttf"

    private fun init(attrs: AttributeSet?) {
        attrs?.also {
            context.theme.obtainStyledAttributes(attrs, R.styleable.OtpView, 0, 0).apply {
                try {
                    digitCount =
                        getInteger(R.styleable.OtpView_digit_count, resources.getInteger(R.integer.default_digit_count))
                    digitBackgroundRes =
                        getResourceId(R.styleable.OtpView_digit_background_res_id, R.drawable.default_digit_background)
                    digitSpaceBetween = getDimensionPixelSize(
                        R.styleable.OtpView_digit_space,
                        resources.getDimensionPixelSize(R.dimen.default_digit_space_between)
                    )
                    digitFontSize = getDimension(
                        R.styleable.OtpView_digit_font_size,
                        resources.getDimension(R.dimen.default_digit_font_size)
                    )
                    digitFont = getString(R.styleable.OtpView_digit_font) ?: "kanit_regular.ttf"
                } finally {
                    recycle()
                }
            }
        }

        orientation = HORIZONTAL
        val font = Typeface.createFromAsset(resources.assets, "fonts/$digitFont")
        val maxDigitIndex = digitCount - 1
        for (i in 0..maxDigitIndex) {
            val edt = SquareEditText(context).apply {
                typeface = font
                imeOptions = EditorInfo.IME_ACTION_DONE
                setTextSize(TypedValue.COMPLEX_UNIT_PX, digitFontSize)
                background = AppCompatResources.getDrawable(context, digitBackgroundRes)
                layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f).apply {
                    if (i > 0) {
                        setMargins(digitSpaceBetween, 0, 0, 0)
                    }
                }
            }

            editTexts.add(edt)
            addView(edt)
        }

        initListeners()
    }

    private fun initListeners() {
        editTexts.forEachIndexed { index, edt ->
            if (index != editTexts.lastIndex) {
                if (index == 0) {
                    edt.setSquareEditTextListeners(null, editTexts[index + 1])
                } else {
                    edt.setSquareEditTextListeners(editTexts[index - 1], editTexts[index + 1])
                }
            } else {
                if (index == 0) edt.setSquareEditTextListeners(null, null)
                else edt.setSquareEditTextListeners(editTexts[index - 1], null)
            }
        }
    }


    fun setOnLastBoxWasFilledListener(listener: () -> Unit) {
        val lastBoxIndex = editTexts.lastIndex
        if (lastBoxIndex != -1) {
            editTexts[lastBoxIndex].onLastBoxWasFilled = listener
        }
    }

    fun requestInput() {
        if (editTexts.isNotEmpty()) {
            editTexts[0].requestFocus()
        }
    }
}