package example.lexicalscanner.utils;

data class KeywordFactory(val keyword: String) {
    // *maps keywords to token type
    val keywords =  mapOf(
        "par" to TokenType.VAR,
        "def" to TokenType.DEF,
        "mastsaloves" to TokenType.RETURN,
        "sah" to TokenType.INT,
        "kosa" to TokenType.FLOAT,
        "char" to TokenType.CHAR,
        "kung" to TokenType.IF,
        "kungdeins" to TokenType.ELSE,
        "habang" to TokenType.WHILE,
        "pag" to TokenType.FOR,
        "sa" to TokenType.IN
    )

    fun getKeywordType(word: String): TokenType? {
        return keywords[word]
    }
}
