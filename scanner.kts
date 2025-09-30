// comments starting with * for julia, pls don't forget to delete when defending so sir doesn't judge us\
// haven't yet fully implemented error handling for invalid or malformed lexemes
// *! if part is unfinished

enum class TokenType {
    // grouping tokens
    LEFT_PAR, RIGHT_PAR, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, SEMICOLON,

    // assignment and comparison tokens
    EQUALS, NOT, EQUAL_EQUAL, NOT_EQUAL, LESSER, LESSER_EQUAL, GREATER, GREATER_EQUAL,

    // arithmetic tokens
    PLUS, MINUS, STAR, SLASH,

    // logical operators
    AND, OR,

    // literals
    IDENTIFIER, STRING, NUMBER, BOOL, NULL,

    // keywords
    VAR, DEF, RETURN, INT, FLOAT, CHAR, IF, ELSE, WHILE, FOR,

    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)

class Scanner(private val source: String) {
    private val readTokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    // *maps keywords to token type
    private val keywords =  mapOf(
        "var" to TokenType.VAR,
        "def" to TokenType.DEF,
        "return" to TokenType.RETURN,
        "int" to TokenType.INT,
        "flt" to TokenType.FLOAT,
        "char" to TokenType.CHAR,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "while" to TokenType.WHILE,
        "for" to TokenType.FOR
    )

    fun scanInput(): List<Token> {
        while (!endOfLine()) {
            start = current // *makes sure that line variable is correct and outputs properly
            scanTokens()
        }
        readTokens.add(Token(TokenType.EOF, "", null, line))
        return readTokens
    }

    // *main loop to determine if scanned text is a token
    private fun scanTokens() {
        val curr = next() // *iterates through text, see function definition below
        when (curr) {
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

            '\'' -> charliteral()
            '"' -> string()         //string literals

            else -> when {
                curr.isDigit() -> number()
                curr.isLetter() || curr == '_' -> identifier()
                else -> println("Unexpected character '$curr' at line $line")
            }
        }
    }

    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') next()
        val text = source.substring(start, current)

        when (text) {
            "TRUE", "FALSE" -> addToken(TokenType.BOOL, text.toBoolean())
            "AND" -> addToken(TokenType.AND)
            "OR" -> addToken(TokenType.OR)
            "null" -> addToken(TokenType.NULL, null)

            "var" -> addToken(TokenType.VAR)
            "def" -> addToken(TokenType.DEF)
            "return" -> addToken(TokenType.RETURN)

            "int" -> addToken(TokenType.INT)
            "flt" -> addToken(TokenType.FLOAT)
            "char" -> addToken(TokenType.CHAR)

            "if" -> addToken(TokenType.IF, literal = text)
            "else" -> addToken(TokenType.ELSE, literal = text)
            "while" -> addToken(TokenType.WHILE, literal = text)
            "for" -> addToken(TokenType.FOR, literal = text)

            else ->  addToken(TokenType.IDENTIFIER)
        }
    }

    private fun charliteral() {
        if (endOfLine()){
            println("Unterminated string at line $line")
            return
        }

        val value = next()   // get the character inside ' '
        if (peek() != '\'') {
            println("Invalid char literal at line $line")
            return
        }

        next()
        addToken(TokenType.CHAR, value)
    }

    private fun string() {
        while (peek() != '"' && !endOfLine()) {
            if (peek() == '\n') line++
            next()
        }
        if (endOfLine()) {
            println("Unterminated string at line $line")
            return
        }
        next()
        val value = source.substring(start +1, current -1)
        addToken(TokenType.STRING, value)
    }

    private fun number() {
        while (peek().isDigit()) next()

        if (peek() == '.' && peekNext().isDigit()) {    // if number is a decimal
            next()
            while (peek().isDigit()) next()
        }
        val value = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, value)
    }

    // helpers
    private fun next(): Char = source[current++] // *reads through text
    private fun addToken(type: TokenType, literal: Any? = null) { // *adds token to mutable list of tokens
        val text = source.substring(start, current)
        readTokens.add(Token(type, text, literal, line))
    }
    private fun match(expected: Char): Boolean { // *used in possible multiple character tokens, checks if character after token changes token type
        if (endOfLine()) return false
        if (source[current] != expected) return false
        current++
        return true
    }
    private fun peek(): Char = if (endOfLine()) '\u0000' else source[current] // *peeks at current character
    private fun peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1] // *peeks at next character
    private fun endOfLine(): Boolean = current >= source.length

}

fun main() {
    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        if (line.trim() == "exit") break

        val scanner = Scanner(line)
        val tokens = scanner.scanInput()

        for (token in tokens) {
            println("Token(type = ${token.type}, lexeme = ${token.lexeme}, literal = ${token.literal}, line = ${token.line})")
        }
    }
}

main()

