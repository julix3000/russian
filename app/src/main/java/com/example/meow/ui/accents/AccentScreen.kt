package com.example.meow.ui.accents

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meow.data.AccentRepository

data class AccentCard(
    val word: String,
    val stressedIndex: Int,
    val ruleTitle: String,
    val ruleExplanation: String
)

@Composable
fun AccentScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val sharedPreferences = remember {
        context.getSharedPreferences("ege_prefs_accents", Context.MODE_PRIVATE)
    }
    var maxStreak by remember {
        mutableIntStateOf(sharedPreferences.getInt("max_streak_accents", 0))
    }

    val wordsList = remember {
        AccentRepository.loadAccentsOnce(context)
        AccentRepository.getAllAccents()
    }
    var currentIndex by remember { mutableIntStateOf(0) }

    var selectedVowelIndex by remember { mutableStateOf<Int?>(null) }
    var isChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showRule by remember { mutableStateOf(false) }
    var currentStreak by remember { mutableIntStateOf(0) }

    val currentCard = if (wordsList.isNotEmpty()) wordsList[currentIndex] else null

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (currentCard == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Слова для ударений не найдены", color = Color.Gray)
            }
            return@Surface
        }

        // Вычисляем адаптивный размер шрифта под длину слова
        val wordLength = currentCard.word.length
        val adaptiveFontSize = when {
            wordLength > 15 -> 23.sp
            wordLength > 13 -> 26.sp
            else -> 32.sp
        }
        val selectedAdaptiveSize = when {
            wordLength > 17 -> 25.sp
            wordLength > 15 -> 28.sp
            else -> 34.sp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя строка (прогресс, серия, выход)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Слово ${currentIndex + 1} из ${wordsList.size}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Подряд: $currentStreak",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE)
                        )
                        Text(
                            text = "Рекорд: $maxStreak",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    TextButton(onClick = onBack, contentPadding = PaddingValues(4.dp)) {
                        Text(text = "Выход", color = Color(0xFF6200EE))
                    }
                }
            }

            // Основная карточка со словом
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Выберите гласную с ударением",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        val vowels = setOf('а', 'е', 'ё', 'и', 'о', 'у', 'ы', 'э', 'ю', 'я', 'А', 'Е', 'Ё', 'И', 'О', 'У', 'Ы', 'Э', 'Ю', 'Я')

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            currentCard.word.forEachIndexed { index, char ->
                                val isVowel = vowels.contains(char)

                                if (isVowel) {
                                    val isSelected = selectedVowelIndex == index
                                    val isThisStressed = (index == currentCard.stressedIndex)

                                    // Определяем цвета фона и текста
                                    val backgroundColor = when {
                                        isChecked -> {
                                            if (isThisStressed) Color(0xFFE8F5E9) // Зеленый фон для правильной
                                            else if (isSelected) Color(0xFFFFEBEE) // Красный фон для ошибки
                                            else Color.Transparent
                                        }
                                        else -> {
                                            if (isSelected) Color(0xFFEDE7F6) else Color.Transparent
                                        }
                                    }

                                    val textColor = when {
                                        isChecked -> {
                                            if (isThisStressed) Color(0xFF2E7D32)
                                            else if (isSelected) Color(0xFFC62828)
                                            else Color.DarkGray
                                        }
                                        else -> {
                                            if (isSelected) Color(0xFF6200EE) else Color(0xFF383838)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .defaultMinSize(minWidth = 20.dp, minHeight = 40.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable(enabled = !isChecked) {
                                                selectedVowelIndex = index
                                            }
                                            .background(backgroundColor, RoundedCornerShape(6.dp))
                                            .border(
                                                width = 0.dp, // Рамка полностью убрана
                                                color = Color.Transparent,
                                                shape = RoundedCornerShape(6.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isSelected || (isChecked && isThisStressed)) char.toString().uppercase() else char.toString().lowercase(),
                                            fontSize = if (isSelected || (isChecked && isThisStressed)) selectedAdaptiveSize else adaptiveFontSize,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            modifier = Modifier.padding(horizontal = 0.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = char.toString().lowercase(),
                                        fontSize = adaptiveFontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 1.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isChecked) {
                            val resultColor = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            val resultText = if (isCorrect) "Правильно!" else "Неверно"

                            Text(
                                text = resultText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = resultColor,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { showRule = !showRule },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (showRule) "Скрыть правило" else "Посмотреть правило")
                            }

                            if (showRule) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(text = currentCard.ruleTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = currentCard.ruleExplanation, fontSize = 13.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }

            // Нижняя кнопка управления
            Button(
                onClick = {
                    if (!isChecked) {
                        if (selectedVowelIndex != null) {
                            isCorrect = (selectedVowelIndex == currentCard.stressedIndex)
                            isChecked = true

                            if (isCorrect) {
                                currentStreak++
                                if (currentStreak > maxStreak) {
                                    maxStreak = currentStreak
                                    sharedPreferences.edit().putInt("max_streak_accents", maxStreak).apply()
                                }
                            } else {
                                currentStreak = 0
                            }
                        }
                    } else {
                        if (currentIndex < wordsList.size - 1) {
                            currentIndex++
                            selectedVowelIndex = null
                            isChecked = false
                            showRule = false
                        } else {
                            onBack()
                        }
                    }
                },
                enabled = isChecked || selectedVowelIndex != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            ) {
                Text(
                    text = if (!isChecked) "Ответить" else if (currentIndex < wordsList.size - 1) "Следующее слово" else "Завершить тренировку",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}