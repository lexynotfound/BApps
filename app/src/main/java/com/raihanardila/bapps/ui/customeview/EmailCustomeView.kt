package com.raihanardila.bapps.ui.customeview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.raihanardila.bapps.R

class EmailCustomeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val emailInputLayout: TextInputLayout
    private val emailEditText: TextInputEditText

    init {
        LayoutInflater.from(context).inflate(R.layout.view_email_custom, this, true)
        emailInputLayout = findViewById(R.id.username_input_layout)
        emailEditText = findViewById(R.id.username_input)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmailCustomeView)
        val hintText = attributes.getString(R.styleable.EmailCustomeView_hintText)
        if (!hintText.isNullOrEmpty()) {
            emailInputLayout.hint = hintText
        }
        attributes.recycle()

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateEmail(s.toString())
            }
        })
    }

    private fun validateEmail(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = context.getString(R.string.invalid_email_error)
            emailInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.red)
        } else {
            emailInputLayout.error = null
            emailInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.gray)
        }
    }
}
