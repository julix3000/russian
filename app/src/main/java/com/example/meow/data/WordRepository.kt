package com.example.meow.data

import android.content.Context
import com.example.meow.R
import com.example.meow.ui.card.WordCard
import org.json.JSONObject

object WordRepository {

    private val allWords = mutableListOf<WordCardItem>()

    data class WordCardItem(
        val id: String,
        val word: String,
        val maskedWord: String,
        val missingLetter: String,
        val wordPart: String,
        val ruleTitle: String,
        val ruleExplanation: String,
        val options: List<String>
    )

    fun loadWordsOnce(context: Context) {
        if (allWords.isNotEmpty()) return

        try {
            val inputStream = context.resources.openRawResource(R.raw.words)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // Читаем корневой объект JSON
            val rootObject = JSONObject(jsonString)

            // 1. Собираем правила в удобный словарь (ключ -> текст правила)
            val rulesMap = mutableMapOf<String, Pair<String, String>>()
            val rulesArray = rootObject.optJSONArray("rules")
            if (rulesArray != null) {
                for (i in 0 until rulesArray.length()) {
                    val ruleObj = rulesArray.getJSONObject(i)
                    val ruleId = ruleObj.getString("id")
                    val title = ruleObj.getString("title")
                    val explanation = ruleObj.getString("explanation")
                    rulesMap[ruleId] = Pair(title, explanation)
                }
            }

            // 2. Читаем массив слов из ключа "words"
            val jsonArray = rootObject.optJSONArray("words") ?: return

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val cleanWord = obj.getString("word")
                val masked = obj.getString("maskedWord")
                val missingLetter = obj.optString("missingLetter", "а").lowercase()
                val wordPart = obj.getString("wordPart")
                val ruleId = obj.optString("ruleId", "")

                // Автоматически подставляем правило из словаря по ruleId
                val ruleInfo = rulesMap[ruleId]
                val ruleTitle = ruleInfo?.first ?: "Правило ЕГЭ"
                val ruleExplanation = ruleInfo?.second ?: "Повторите соответствующую орфограмму."

                // Читаем варианты (options) из JSON
                val jsonOptions = obj.optJSONArray("options")
                val optionsList = mutableListOf<String>()

                if (jsonOptions != null) {
                    for (j in 0 until jsonOptions.length()) {
                        optionsList.add(jsonOptions.getString(j).lowercase())
                    }
                }

                val finalOptions = if (optionsList.isNotEmpty()) {
                    optionsList.shuffled()
                } else {
                    listOf(missingLetter, "а").distinct().shuffled()
                }

                allWords.add(
                    WordCardItem(
                        id = obj.optString("id", "w_$i"),
                        word = cleanWord,
                        maskedWord = masked,
                        missingLetter = missingLetter,
                        wordPart = wordPart,
                        ruleTitle = ruleTitle,
                        ruleExplanation = ruleExplanation,
                        options = finalOptions
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCardsForCategories(categories: Set<String>): List<WordCard> {
        val filtered = allWords.filter { categories.contains(it.wordPart) }

        return filtered.map { item ->
            WordCard(
                word = item.word,
                maskedWord = item.maskedWord,
                missingLetter = item.missingLetter,
                ruleTitle = item.ruleTitle,
                ruleExplanation = item.ruleExplanation,
                options = item.options
            )
        }.shuffled()
    }
}