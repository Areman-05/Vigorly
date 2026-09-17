package com.example.vigorly.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent

val AuthFieldShape = RoundedCornerShape(18.dp)

@Composable
fun AuthGlassInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String? = null,
    onFocusChange: (Boolean) -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    var focused by remember { mutableStateOf(false) }
    val labelColor = when {
        isError -> PrimaryAccent
        focused -> PrimaryAccent
        else -> GlassLabel.copy(alpha = 0.7f)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = LabelCaps.copy(fontSize = 11.sp, letterSpacing = 1.4.sp),
            color = labelColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = AuthFieldShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .then(
                        if (isError) {
                            Modifier.border(
                                BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.75f)),
                                AuthFieldShape
                            )
                        } else {
                            Modifier
                        }
                    )
                    .padding(start = 16.dp, end = if (trailing != null) 4.dp else 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { state ->
                                focused = state.isFocused
                                onFocusChange(state.isFocused)
                            }
                            .then(modifier)
                            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
                        singleLine = true,
                        textStyle = BodyMd.copy(
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            color = OnSurface
                        ),
                        cursorBrush = SolidColor(PrimaryAccent),
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        decorationBox = { inner ->
                            Box {
                                if (value.isEmpty() && !placeholder.isNullOrBlank()) {
                                    Text(
                                        text = placeholder,
                                        style = BodyMd.copy(fontSize = 16.sp, lineHeight = 22.sp),
                                        color = GlassLabel.copy(alpha = 0.38f)
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }
                trailing?.invoke()
            }
        }
    }
}
