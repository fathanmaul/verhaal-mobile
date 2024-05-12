package dev.rushia.verhaal_mobile.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import dev.rushia.verhaal_mobile.R

class Button : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var enabledBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
    private var disabledBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.bg_button_disabled) as Drawable

    init {
        setTypeface(null, Typeface.BOLD)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        textSize = 14f
        gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = context.resources.getFont(R.font.poppins_bold)
        }
    }
}