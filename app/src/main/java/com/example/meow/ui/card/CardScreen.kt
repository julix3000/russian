package com.example.meow.ui.card

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.meow.data.WordRepository

data class WordCard(
    val word: String,
    val maskedWord: String,
    val missingLetter: String,
    val ruleTitle: String,
    val ruleExplanation: String,
    val options: List<String>
)

@Composable
fun CardScreen(
    selectedCategories: Set<String>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Работа с сохраненным рекордом через SharedPreferences
    val sharedPreferences = remember {
        context.getSharedPreferences("ege_prefs", Context.MODE_PRIVATE)
    }
    var maxStreak by remember {
        mutableIntStateOf(sharedPreferences.getInt("max_streak", 0))
    }

    val wordsList = remember(selectedCategories) {
        WordRepository.loadWordsOnce(context)
        WordRepository.getCardsForCategories(selectedCategories)
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showRule by remember { mutableStateOf(false) }

    // Счетчики
    var currentStreak by remember { mutableIntStateOf(0) }

    val currentCard = if (wordsList.isNotEmpty()) wordsList[currentIndex] else null

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (currentCard == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Слова для выбранных разделов не найдены", color = Color.Gray)
            }
            return@Surface
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя строка: прогресс, счетчики серии и кнопка выхода
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

                // Счётчики серий в правом верхнем углу
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

            // Основная карточка
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
                            text = "Выберите правильный вариант для пропуска",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Вычисляем общую длину слова для адаптивного размера шрифта, чтобы длинные слова влезали
                        val wordLength = currentCard.maskedWord.length
                        val adaptiveFontSize = when {
                            wordLength > 17 -> 23.sp
                            wordLength > 15 -> 26.sp
                            else -> 32.sp
                        }
                        val adaptiveBoxHeight = when {
                            wordLength > 12 -> 42.dp
                            wordLength > 9 -> 46.dp
                            else -> 52.dp
                        }

                        // Слово с интерактивным окошком пропуска (с горизонтальной прокруткой на случай сверхдлинных слов)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val underscoreIndex = currentCard.maskedWord.indexOf('_').let { if (it == -1) 0 else it }
                            val partBefore = if (currentCard.maskedWord.contains('_')) currentCard.maskedWord.substring(0, underscoreIndex) else currentCard.maskedWord
                            val partAfter = if (currentCard.maskedWord.contains('_')) currentCard.maskedWord.substring(underscoreIndex + 1) else ""

                            Text(
                                text = partBefore,
                                fontSize = adaptiveFontSize,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 1.sp
                            )

                            // Окошко пропуска
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .defaultMinSize(minWidth = 38.dp)
                                    .height(adaptiveBoxHeight)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChecked) Color(0xFFF5F5F5) else Color(0xFFEDE7F6))
                                    .border(
                                        2.dp,
                                        if (isChecked) Color.LightGray else Color(0xFF6200EE),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isChecked) {
                                        currentCard.missingLetter.uppercase()
                                    } else {
                                        selectedOption?.uppercase() ?: "_"
                                    },
                                    fontSize = adaptiveFontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) {
                                        if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    } else {
                                        Color(0xFF6200EE)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }

                            Text(
                                text = partAfter,
                                fontSize = adaptiveFontSize,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Кнопки с вариантами выбора
                        if (!isChecked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                currentCard.options.forEach { option ->
                                    val isSelected = selectedOption == option
                                    Button(
                                        onClick = { selectedOption = option },
                                        modifier = Modifier
                                            .padding(horizontal = 6.dp)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) Color(0xFF6200EE) else Color(0xFFF3E5F5)
                                        )
                                    ) {
                                        Text(
                                            text = option.uppercase(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF6200EE)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (isChecked) {
                            val resultColor = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            val resultText = if (isCorrect) "Правильно!" else "Неверно. Правильный ответ: ${currentCard.missingLetter.uppercase()}"

                            Text(
                                text = resultText,
                                fontSize = 16.sp,
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

            // Нижняя кнопка подтверждения
            Button(
                onClick = {
                    if (!isChecked) {
                        if (selectedOption != null) {
                            isCorrect = selectedOption!!.trim().equals(currentCard.missingLetter.trim(), ignoreCase = true)
                            isChecked = true

                            if (isCorrect) {
                                currentStreak++
                                if (currentStreak > maxStreak) {
                                    maxStreak = currentStreak
                                    sharedPreferences.edit().putInt("max_streak", maxStreak).apply()
                                }
                            } else {
                                currentStreak = 0
                            }
                        }
                    } else {
                        if (currentIndex < wordsList.size - 1) {
                            currentIndex++
                            selectedOption = null
                            isChecked = false
                            showRule = false
                        } else {
                            onBack()
                        }
                    }
                },
                enabled = isChecked || selectedOption != null,
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