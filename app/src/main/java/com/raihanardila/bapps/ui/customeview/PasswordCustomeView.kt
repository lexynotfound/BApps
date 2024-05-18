package com.raihanardila.bapps.ui.customeview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.raihanardila.bapps.R

class PasswordCustomeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val passwordInputLayout: TextInputLayout
    private val passwordEditText: TextInputEditText
    private var isPasswordVisible = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_password_custom, this, true)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        passwordEditText = findViewById(R.id.password_input)

        passwordInputLayout.setEndIconOnClickListener {
            isPasswordVisible = !isPasswordVisible
            updatePasswordVisibility()
        }

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })
    }

    private fun updatePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordInputLayout.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.carbon_view_filled)
        } else {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInputLayout.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.carbon_view_off_filled)
        }
        passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
    }

    private fun validatePassword(password: String) {
        if (password.length < 6) {
            passwordInputLayout.error = context.getString(R.string.invalid_password_error)
            passwordInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.red)
        } else {
            passwordInputLayout.error = null
            passwordInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.gray)
        }
    }
}
