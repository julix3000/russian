package com.example.meow.data

import android.content.Context
import com.example.meow.R
import com.example.meow.ui.accents.AccentCard
import org.json.JSONArray

object AccentRepository {
    private var accentsList: List<AccentCard> = emptyList()

    fun loadAccentsOnce(context: Context) {
        if (accentsList.isNotEmpty()) return

        try {
            val inputStream = context.resources.openRawResource(R.raw.accents)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val list = mutableListOf<AccentCard>()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val word = obj.getString("word")
                val stressedIndex = obj.getInt("stressedIndex")
                val ruleTitle = obj.getString("ruleTitle")
                val ruleExplanation = obj.getString("ruleExplanation")

                list.add(AccentCard(word, stressedIndex, ruleTitle, ruleExplanation))
            }

            accentsList = list
        } catch (e: Exception) {
            e.printStackTrace()
            accentsList = emptyList()
        }
    }

    fun getAllAccents(): List<AccentCard> {
        return accentsList
    }
}