package com.example.meow.ui.theme
import androidx.compose.ui.tooling.preview.Preview

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Здесь настраивается палитра цветов приложения
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFDE2FD4),       // Главный фирменный цвет (основные кнопки, активные переключатели, важные акценты)
    onPrimary = Color.White,            // Цвет текста и иконок, которые находятся НАУХОДЯТСЯ на кнопках цвета primary
    secondary = Color(0xFF03DAC6),      // Второстепенный акцентный цвет (для дополнительных кнопок или элементов)
    background = Color(0xFFFAFAFA),     // Цвет фона самого экрана (самая подложка приложения)
    surface = Color.White,              // Цвет карточек, блоков и верхних панелей (поверхностей)
    onSurface = Color(0xFF212121),      // Цвет основного текста, который пишется НА карточках и поверхностях
)

// 2. Сама функция темы, которая применяет эти цвета ко всему приложению


@Composable
fun EgeRussianTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    // Блок настройки верхней системной панели телефона (status bar)
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Делает иконки в строке состояния (чате, батарейке) темными, чтобы их было видно на светлом фоне
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    // Передаем нашу палитру в стандартный компонент MaterialTheme,
    // чтобы все кнопки и тексты в приложении автоматически подхватили эти цвета
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

