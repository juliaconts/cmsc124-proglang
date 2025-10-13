package example.lexicalscanner.utils

class Scanner(val source: String) {
    val readTokens = mutableListOf<Token>()
    var start = 0
    var current = 0
    var line = 1

//    val keyword = KeywordFactory(source)

    fun scanInput(): List<Token> {
        while (!endOfLine()) {
            start = current // *makes sure that line variable is correct and outputs properly
            scanTokens()
        }
        readTokens.add(Token(TokenType.EOF, "", null, line))
        return readTokens
    }

    // *main loop to determine if scanned text is a token
    fun scanTokens() {
        when (val curr = next()) { // *iterates through text, see function definition below
            // single-character tokens
            '(' -> addToken(TokenType.LEFT_PAR)
            ')' -> addToken(TokenType.RIGHT_PAR)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            ';' -> addToken(TokenType.SEMICOLON)
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '*' -> addToken(TokenType.STAR)
            '/' -> addToken(TokenType.SLASH)

            // possible multiple-character tokens
            '!' -> addToken(if (match('=')) TokenType.NOT_EQUAL else TokenType.NOT)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUALS)
            '<' -> addToken(if (match('=')) TokenType.LESSER_EQUAL else TokenType.LESSER)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

            // comments
            '#' ->   { // *! multiple line comments
                while (peek() != '\n' && !endOfLine()) next()
            }

            //whitespace
            ' ', '\r', '\t' -> {}   //ignore spaces
            '\n' -> line++          //count lines

            '\'' -> charLiteral()
            '"' -> string()         //string literals

            else -> when {
                curr.isDigit() -> number()
                curr.isLetter() || curr == '_' -> identifier()
                else -> println("Awit par, unexpected character '$curr' at line $line")
            }
        }
    }

    fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') next()
        when (val text = source.substring(start, current)) {
            "FRFR", "CAP" -> addToken(TokenType.BOOL, text.toBoolean())
            "AND" -> addToken(TokenType.AND)
            "OR" -> addToken(TokenType.OR)
            "null" -> addToken(TokenType.NULL, null)

            "par" -> addToken(TokenType.VAR)
            "def" -> addToken(TokenType.DEF)
            "matsaloves" -> addToken(TokenType.RETURN)

            "sah" -> addToken(TokenType.INT)
            "kosa" -> addToken(TokenType.FLOAT)
            "char" -> addToken(TokenType.CHAR)
            "dol" -> addToken(TokenType.BOOL)

            "kung" -> addToken(TokenType.IF, literal = text)
            "kungdeins" -> addToken(TokenType.ELSE, literal = text)
            "habang" -> addToken(TokenType.WHILE, literal = text)
            "pag" -> addToken(TokenType.FOR, literal = text)

            else ->  addToken(TokenType.IDENTIFIER)
        }
    }

    fun charLiteral() {
        if (endOfLine()){
            println("Awit ka par, unterminated string at line $line")
            return
        }

        val value = next()   // get the character inside ' '
        if (peek() != '\'') {
            println("Awit lods, invalid char literal at line $line")
            return
        }

        next()
        addToken(TokenType.CHAR, value)
    }

    fun string() {
        while (peek() != '"' && !endOfLine()) {
            if (peek() == '\n') line++
            next()
        }
        if (endOfLine()) {
            println("Awit ka sah, unterminated string at line $line")
            return
        }
        next()
        val value = source.substring(start +1, current -1)
        addToken(TokenType.STRING, value)
    }

    fun number() {
        while (peek().isDigit()) next()

        if (peek() == '.' && peekNext().isDigit()) {    // if number is a decimal
            next()
            while (peek().isDigit()) next()
        }
        val value = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, value)
    }
}



