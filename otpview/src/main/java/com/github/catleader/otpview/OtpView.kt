package com.github.catleader.otpview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat

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
     * Listener when user reach the last box.
     */
    var onReachingEndOfBox: (() -> Unit)? = null
        set(value) {
            if (editTexts.isNotEmpty()) {
                val lastEdt = editTexts[editTexts.lastIndex]
                lastEdt.onReachingEnd = value
            }
            field = value
        }

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
    var digitFontRes = R.font.kanit_regular

    private fun init(attrs: AttributeSet?) {
        attrs?.also {
            context.theme.obtainStyledAttributes(attrs, R.styleable.OtpView, 0, 0).apply {
                try {
                    digitCount = getInteger(R.styleable.OtpView_digit_count, resources.getInteger(R.integer.default_digit_count))
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
                    digitFontRes = getResourceId(R.styleable.OtpView_digit_font, R.font.kanit_regular)
                } finally {
                    recycle()
                }
            }
        }

        orientation = HORIZONTAL
        val font = ResourcesCompat.getFont(context, digitFontRes)
        val maxDigitIndex = digitCount - 1
        for (i in 0..maxDigitIndex) {
            val edt = SquareEditText(context).apply {
                typeface = font
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

    fun requestOtpFocus() {
        if (editTexts.isNotEmpty()) {
            editTexts[0].requestFocus()
        }
    }
}