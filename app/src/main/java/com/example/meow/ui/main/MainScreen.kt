package com.example.meow.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onStartTraining: (selectedCategories: Set<String>) -> Unit,
    onStartAccents: () -> Unit
) {
    // 1. Изначально все галочки сняты (false)
    var rootsSelected by remember { mutableStateOf(false) }
    var prefixesSelected by remember { mutableStateOf(false) }
    var suffixesSelected by remember { mutableStateOf(false) }

    val allSelected = rootsSelected && prefixesSelected && suffixesSelected
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "ЕГЭ по русскому языку",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Выберите направление тренировки",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- БЛОК 1: ОРФОГРАФИЯ ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFAFAFA))
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Орфография",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    CategoryCard(
                        title = "Корни",
                        isSelected = rootsSelected,
                        onClick = {
                            rootsSelected = !rootsSelected
                            errorMessage = null
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryCard(
                        title = "Приставки",
                        isSelected = prefixesSelected,
                        onClick = {
                            prefixesSelected = !prefixesSelected
                            errorMessage = null
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryCard(
                        title = "Суффиксы",
                        isSelected = suffixesSelected,
                        onClick = {
                            suffixesSelected = !suffixesSelected
                            errorMessage = null
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryCard(
                        title = "Всё вместе (Орфография)",
                        isSelected = allSelected,
                        onClick = {
                            val newState = !allSelected
                            rootsSelected = newState
                            prefixesSelected = newState
                            suffixesSelected = newState
                            errorMessage = null
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- БЛОК 2: УДАРЕНИЯ (В стиле обычных карточек: белый фон, серая рамка, черный текст) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFAFAFA))
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Орфоэпия",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White) // Белый фон, как у невыбранных карточек
                            .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)) // Серая обводка
                            .clickable { onStartAccents() }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ударения",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121) // Черный/темный текст
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Главная кнопка старта орфографии
            Button(
                onClick = {
                    val selected = mutableSetOf<String>()
                    if (rootsSelected) selected.add("ROOT")
                    if (prefixesSelected) selected.add("PREFIX")
                    if (suffixesSelected) selected.add("SUFFIX")

                    if (selected.isEmpty()) {
                        errorMessage = "Выберите хотя бы один раздел орфографии!"
                    } else {
                        onStartTraining(selected)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(
                    text = "Начать тренировку орфографии",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFEDE7F6) else Color.White
    val borderColor = if (isSelected) Color(0xFF6200EE) else Color(0xFFE0E0E0)
    val textColor = if (isSelected) Color(0xFF6200EE) else Color(0xFF212121)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            RadioButton(
                selected = isSelected,
                onClick = null
            )
        }
    }
}