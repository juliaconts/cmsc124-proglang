package example.lexicalscanner.syntax

import example.lexicalscanner.utils.*

class Parser(val tokens: List<Token>) {
    var current = 0

    fun parse() {
        while(!endOfLine()) {
            declaration()
        }
    }

    fun declaration() {
        when {
            match(TokenType.VAR) -> varDeclaration()
            match(TokenType.DEF) -> funcDeclaration()
            else -> statement()
        }
    }

    fun varDeclaration() {
        consume(TokenType.IDENTIFIER, "Variable name dapat 'yan pagkatapos ng 'par', par.")

        if (match(TokenType.EQUALS)) {
            expression()
        }

        consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos ng variable declaration, rops.")
    }

    fun funcDeclaration() {
        consume(TokenType.IDENTIFIER, "Function name dapat 'yan pagkatapos ng 'def', sah.")
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng pangalan ng function, pre.")
        if (!check(TokenType.RIGHT_PAR)) {
            do {
                consume(TokenType.IDENTIFIER, "'San parameter name mo, boss?")
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng pangalan ng function, pre.")
        block()
    }

    fun statement() {
        when {
            match(TokenType.IF) -> ifStatement()
            match(TokenType.WHILE) -> whileStatement()
            match(TokenType.FOR) -> forStatement()
            match(TokenType.RETURN) -> returnStatement()
            match(TokenType.LEFT_BRACE) -> block()
            else -> expressionStatement()
        }
    }

    fun ifStatement() {
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng 'kung', ssob.")
        expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng condition, ssob.")
        statement()
        if (match(TokenType.ELSE)) {
            statement()
        }
    }

    fun whileStatement() {
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng 'habang', ssob.")
        expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng condition, ssob.")
        statement()
    }

    fun forStatement() {
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng 'pag', ssob.")

        if (!match(TokenType.SEMICOLON)) {
            if (match(TokenType.VAR)) varDeclaration()
            else expressionStatement()
        }

        if (!check(TokenType.RIGHT_PAR)) expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng mga expression, ssob.")
        statement()
    }

    fun returnStatement() {
        if(!check(TokenType.SEMICOLON)) expression()
        consume(TokenType.SEMICOLON, "May ';' dapat pagakatapos ng 'matsaloves', pre.")
    }

    fun expressionStatement() {
        expression()
        consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos niyan, lods.")
    }

    fun block() {
        while (!check(TokenType.RIGHT_BRACE) && !endOfLine()) {
            declaration()
        }
        consume(TokenType.RIGHT_BRACE, "May '}' dapat pagkatapos niyan, pre.")
    }

    fun expression() {
        equality()
    }

    fun equality() {
        comparison()
        while (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            comparison()
        }
    }

    fun comparison() {
        term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESSER, TokenType.LESSER_EQUAL)) {
            term()
        }
    }

    fun term() {
        factor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            factor()
        }
    }

    fun factor() {
        unary()
        while (match(TokenType.STAR, TokenType.SLASH)){
            unary()
        }
    }

    fun unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) unary()
        else primary()
    }

    fun primary() {
        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR, TokenType.BOOL, TokenType.NULL, TokenType.IDENTIFIER)) return
        if (match(TokenType.LEFT_PAR)) {
            expression()
            consume(TokenType.RIGHT_PAR, "May ')' dapat pagkatapos ng expression, trops.")
            return
        }

        error(peek(), "May expression dapat 'jan, lods.")
    }

    class ParseError : RuntimeException()
}


