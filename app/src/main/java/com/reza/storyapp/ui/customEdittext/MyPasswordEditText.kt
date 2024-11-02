package com.reza.storyapp.ui.customEdittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.reza.storyapp.R

class MyPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) { //not needed

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.toString().length < 8) {
                    setError(context.getString(R.string.invalid_password_error), null)
                } else {
                    setError(null, null)
                }
            }

            override fun afterTextChanged(s: Editable?) { //not needed

            }

        })
    }
}