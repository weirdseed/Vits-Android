package com.example.moereng.utils.text

import android.content.res.AssetManager

class ChineseTextUtils(
    override val symbols: List<String>,
    override val cleanerName: String,
    override val assetManager: AssetManager
) : TextUtils {

    private val splitSymbols = listOf(
        ".", "。", "…", ",", "，"
    )

    override fun cleanInputs(text: String): String {
        var tempText = text
        splitSymbols.forEach {
            tempText = tempText.replace(it, "\n")
        }
        return tempText
    }

    override fun splitSentence(text: String): List<String> {
        return text.split("\n").filter { it.isNotEmpty() }
    }

    override fun wordsToLabels(text: String): IntArray {
        val labels = ArrayList<Int>()
        labels.add(0)

        // symbol to id
        val symbolToIndex = HashMap<String, Int>()
        symbols.forEachIndexed { index, s ->
            symbolToIndex[s] = index
        }

        // clean text
        var cleanedText = ""

        if (cleanerName == "chinese_cleaners") {
            val cleaner = ChineseCleaners()
            cleanedText = cleaner.chinese_clean_text1(text)
        } else if (cleanerName == "chinese_cleaners2") {
            // TODO
        }

        // symbol to label
        for (symbol in cleanedText) {
            if (!symbols.contains(symbol.toString())) {
                continue
            }
            val label = symbolToIndex[symbol.toString()]
            if (label != null) {
                labels.add(label)
                labels.add(0)
            }
        }
        return labels.toIntArray()
    }

    override fun convertSentenceToLabels(
        text: String
    ): List<IntArray> {
        val outputs = ArrayList<IntArray>()
        val sentences = splitSentence(text)
        for (sentence in sentences) {
            val labels = wordsToLabels(sentence)
            outputs.add(labels)
        }
        return outputs
    }

    override fun convertText(
        text: String
    ): List<IntArray> {
        // clean inputs
        val cleanedInputs = cleanInputs(text)

        // convert inputs
        return convertSentenceToLabels(cleanedInputs)
    }
}

