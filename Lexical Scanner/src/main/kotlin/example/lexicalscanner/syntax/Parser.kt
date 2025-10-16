package example.lexicalscanner.syntax

import example.lexicalscanner.utils.*

class Parser(val tokens: List<Token>) {
    var current = 0

    fun parse(): List<Stmt> {
        return try {
            program()
        } catch (error: ParseError) {
            synchronize()
            emptyList<Stmt>()
        }
    }

    fun program(): List<Stmt> {
        val statements = mutableListOf<Stmt>()

        while (!endOfLine()) {
            try {
                statements.add(declaration())
            } catch (error: ParseError) {
                synchronize()
            }
        }
        return statements
    }

    fun declaration(): Stmt {
        return try {
            when {
                match(TokenType.VAR) -> varDeclaration()
                match(TokenType.DEF) -> funcDeclaration()
                else -> statement()
            }
        } catch (e: ParseError) {
            synchronize()
            Stmt.ExpressionStmt(Expr.Literal(" "))
        }
    }

    fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Variable name dapat 'yan pagkatapos ng 'par', par.")
        var initializer: Expr? = null
        if (match(TokenType.EQUALS)) {
            initializer = expression()
        }
        consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos ng variable declaration, rops.")
        return Stmt.VariableDecl(name,initializer)
    }

    fun funcDeclaration(): Stmt.FunctionStmt {
        val name = consume(TokenType.IDENTIFIER, "Function name dapat 'yan pagkatapos ng 'def', sah.")
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng pangalan ng function, pre.")

        val parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAR)) {
            do {
                if (parameters.size >= 255) {
                    error(peek(), "Sumobra kana boss.")
                }
                parameters.add(consume(TokenType.IDENTIFIER, "'San parameter name mo, boss?"))
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng pangalan ng function, pre.")
        val body = block()
        return Stmt.FunctionStmt(name, parameters, body)
    }

    fun statement(): Stmt {
        return when {
            match(TokenType.IF) -> ifStatement()
            match(TokenType.WHILE) -> whileStatement()
            match(TokenType.FOR) -> forStatement()
            match(TokenType.RETURN) -> returnStatement()
            match(TokenType.LEFT_BRACE) -> Stmt.Block(block())
            else -> expressionStatement()
        }
    }

    fun ifStatement(): Stmt {
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng 'kung', ssob.")
        val condition = expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng condition, ssob.")
        val thenBranch = statement()
        val elseBranch =
            if (match(TokenType.ELSE)) {
                statement()
            } else {
                null
            }
        return Stmt.IfStmt(condition, thenBranch, elseBranch)
    }

    fun whileStatement(): Stmt {
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng 'habang', ssob.")
        val condition = expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng condition, ssob.")
        val body = statement()
        return Stmt.WhileStmt(condition, body)
    }

    fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAR, "May '(' dapat 'yan pagkatapos ng 'pag', ssob.")

        val variable = consume(TokenType.IDENTIFIER, "Kailangan ng pangalan yan, pre")
        consume(TokenType.IN, "May 'sa' pa yan, pre")
        val iterable = expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat 'yan pagkatapos ng mga expression, ssob.")
        val body = statement()
        return Stmt.ForStmt(variable, iterable, body)
    }

    fun returnStatement(): Stmt {
        val keyword = previous()
        val value = if (!check(TokenType.SEMICOLON)) {
            expression()
        } else {
            null
        }
        consume(TokenType.SEMICOLON, "May ';' dapat pagakatapos ng 'matsaloves', pre.")
        return Stmt.ReturnStmt(keyword, value)
    }

    fun expressionStatement(): Stmt {
        return try {
            val expr = expression()
            consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos niyan, lods.")
            Stmt.ExpressionStmt(expr)
        } catch (e: ParseError) {
            synchronize()
            Stmt.ExpressionStmt(Expr.Literal("error"))
        }
    }

    fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.RIGHT_BRACE) && !endOfLine()) {
            statements.add(declaration())
        }
        consume(TokenType.RIGHT_BRACE, "May '}' dapat pagkatapos niyan, pre.")
        return statements
    }

    fun expression(): Expr {
        return equality()
    }

    fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr,operator, right)
        }
        return expr
    }

    fun comparison(): Expr {
        var expr = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESSER, TokenType.LESSER_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    fun term(): Expr {
        var expr = factor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)){
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    fun unary(): Expr {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    fun primary(): Expr {
        return when {
            match(
                TokenType.NUMBER, TokenType.STRING, TokenType.CHAR,
                TokenType.TRUE, TokenType.FALSE, TokenType.NULL
            ) -> {
                Expr.Literal(previous().literal ?: previous().lexeme)
            }
            match(TokenType.IDENTIFIER) -> {
                Expr.Variable(previous())
            }
            match(TokenType.LEFT_PAR) -> {
                val expr = expression()
                if (!check(TokenType.RIGHT_PAR)) {
                    error(peek(), "May ')' dapat pagkatapos ng expression, trops.")
                    throw ParseError()
                }
                consume(TokenType.RIGHT_PAR, "Expected ')'")
                Expr.Grouping(expr)
            }
            else -> {
                // Check specifically for stray closing parenthesis
                if (check(TokenType.RIGHT_PAR)) {
                    error(peek(), "Walang kapares na '(' itong ')', trops.")
                    throw ParseError()
                }
                // No valid token found for an expression
                error(peek(), "May expression dapat 'jan, lods.")
                throw ParseError()
            }
        }
    }

    class ParseError : RuntimeException()
}