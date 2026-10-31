package com.example.meow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.meow.ui.card.CardScreen
import com.example.meow.ui.main.MainScreen
import com.example.meow.ui.theme.EgeRussianTheme
import com.example.meow.ui.accents.AccentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EgeRussianTheme {
                var currentScreen by remember { mutableStateOf("main") }
                var selectedCategories by remember { mutableStateOf(setOf<String>()) }

                when (currentScreen) {
                    "main" -> {
                        MainScreen(
                            onStartTraining = { categories ->
                                selectedCategories = categories
                                currentScreen = "card"
                            },
                            onStartAccents = {
                                // Здесь будет переключение на экран ударений, например:
                                currentScreen = "accents"
                            }

                        )
                    }
                    "card" -> {
                        CardScreen(
                            selectedCategories = selectedCategories,
                            onBack = {
                                currentScreen = "main"
                            }
                        )
                    }
                    "accents" -> {
                        AccentScreen(
                            onBack = {
                                currentScreen = "main"
                            }
                        )
                    }

                }
            }
        }
    }
}