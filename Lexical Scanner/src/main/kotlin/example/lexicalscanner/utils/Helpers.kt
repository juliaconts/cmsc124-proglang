package example.lexicalscanner.utils

import example.lexicalscanner.syntax.Parser

// Scanner helper functions
fun Scanner.peek(): Char = if (endOfLine()) '\u0000' else source[current] // *peeks at current character
fun Scanner.peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1] // *peeks at next character
fun Scanner.endOfLine(): Boolean = current >= source.length
fun Scanner.next(): Char = source[current++] // *reads through text
fun Scanner.addToken(type: TokenType, literal: Any? = null) { // *adds token to mutable list of tokens
    val text = source.substring(start, current)
    readTokens.add(Token(type, text, literal, line))
}
fun Scanner.match(expected: Char): Boolean { // *used in possible multiple character tokens, checks if character after token changes token type
    if (endOfLine()) return false
    if (source[current] != expected) return false
    current++
    return true
}

// Parser helper functions
fun Parser.peek(): Token = tokens[current]
fun Parser.endOfLine(): Boolean = peek().type == TokenType.EOF
fun Parser.previous(): Token = tokens[current - 1]
fun Parser.check(type: TokenType): Boolean {
    if (endOfLine()) return false else return peek().type == type
}
fun Parser.next(): Token {
    if (!endOfLine()) current++
    return previous()
}
fun Parser.match(vararg types: TokenType): Boolean {
    for (type in types) {
        if (check(type)) {
            next()
            return true
        }
    }
    return false
}
fun error(token: Token, message: String){
    println("Awit ka pre, may syntax error ka sa '${token.lexeme}' sa  ika-${token.line} na linya: $message")
    throw Parser.ParseError()
}
fun Parser.consume(type: TokenType, message: String): Token {
    if (check(type)) return next()
    error(peek(), message)
    return previous()
}
fun Parser.synchronize() {
    next()
    while (!endOfLine()) {
        if (previous().type == TokenType.SEMICOLON) return
        when (peek().type) {
            TokenType.IF, TokenType.WHILE, TokenType.RETURN, TokenType.VAR, TokenType.FOR -> return
            else -> next()
        }
    }
}