package example.lexicalscanner.syntax

import example.lexicalscanner.utils.*

class Parser(val tokens: List<Token>) {
    var current = 0

    fun parse(): Stmt {
        return try {
            Stmt.Program(declaration())
        } catch (error: ParseError) {
            synchronize()
            Stmt.Program(Stmt.ExpressionStmt(Expr.Literal("error")))
        }
    }

    private fun declaration(): Stmt {
        return try {
            when {
                match(TokenType.VAR) -> varDeclaration()
                match(TokenType.DEF) -> funcDeclaration()
                else -> statement()
            }
        } catch (e: ParseError) {
            synchronize()
            Stmt.ExpressionStmt(Expr.Literal("error"))
        }
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Variable name expected after 'par', pre.").token
        val initializer = if (match(TokenType.EQUALS)) expression() else null
        consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos ng variable declaration, rops.")
        return Stmt.VariableDecl(name, initializer)
    }

    private fun funcDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Function name expected after 'def', sah.").token
        consume(TokenType.LEFT_PAR, "May '(' dapat pagkatapos ng pangalan ng function, pre.")

        val param = if (!check(TokenType.RIGHT_PAR)) consume(TokenType.IDENTIFIER, "Parameter name missing, boss.").token else null
        consume(TokenType.RIGHT_PAR, "May ')' dapat pagkatapos ng parameters, pre.")

        val body = statement()
        return Stmt.FunctionStmt(name, param, null, body)
    }

    private fun statement(): Stmt = when {
        match(TokenType.IF) -> ifStatement()
        match(TokenType.WHILE) -> whileStatement()
        match(TokenType.FOR) -> forStatement()
        match(TokenType.RETURN) -> returnStatement()
        match(TokenType.LEFT_BRACE) -> block()
        else -> expressionStatement()
    }

    private fun ifStatement(): Stmt {
        consume(TokenType.LEFT_PAR, "May '(' dapat pagkatapos ng 'kung', ssob.")
        val condition = expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat pagkatapos ng condition, ssob.")
        val thenBranch = statement()
        val elseBranch = if (match(TokenType.ELSE)) statement() else null
        return Stmt.IfStmt(condition, thenBranch, elseBranch)
    }

    private fun whileStatement(): Stmt {
        consume(TokenType.LEFT_PAR, "May '(' dapat pagkatapos ng 'habang', ssob.")
        val condition = expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat pagkatapos ng condition, ssob.")
        val body = statement()
        return Stmt.WhileStmt(condition, body)
    }

    private fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAR, "May '(' dapat pagkatapos ng 'pag', ssob.")
        val variable = consume(TokenType.IDENTIFIER, "Variable name missing, pre.").token
        consume(TokenType.IN, "May 'sa' dapat sa pagitan, pre.")
        val iterable = expression()
        consume(TokenType.RIGHT_PAR, "May ')' dapat pagkatapos ng expression, ssob.")
        val body = statement()
        return Stmt.ForStmt(variable, iterable, body)
    }

    private fun returnStatement(): Stmt {
        val keyword = previous().token
        val value = if (!check(TokenType.SEMICOLON)) expression() else null
        consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos ng 'matsaloves', pre.")
        return Stmt.ReturnStmt(keyword, value)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.SEMICOLON, "May ';' dapat pagkatapos niyan, lods.")
        return Stmt.ExpressionStmt(expr)
    }

    private fun block(): Stmt {
        if (check(TokenType.RIGHT_BRACE)) {
            consume(TokenType.RIGHT_BRACE, "May '}' dapat pagkatapos niyan, pre.")
            return Stmt.Block(null, null)
        }

        val first = declaration()
        val next = if (!check(TokenType.RIGHT_BRACE) && !endOfLine()) block() else null
        consume(TokenType.RIGHT_BRACE, "May '}' dapat pagkatapos niyan, pre.")
        return Stmt.Block(first, next)
    }

    private fun expression(): Expr = equality()

    private fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            val operator = previous().token
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESSER, TokenType.LESSER_EQUAL)) {
            val operator = previous().token
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous().token
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = previous().token
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            val operator = previous().token
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr = when {
        match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR, TokenType.TRUE, TokenType.FALSE, TokenType.NULL) ->
            Expr.Literal(previous().token.literal ?: previous().token.lexeme)

        match(TokenType.IDENTIFIER) -> Expr.Variable(previous().token)

        match(TokenType.LEFT_PAR) -> {
            val expr = expression()
            consume(TokenType.RIGHT_PAR, "May ')' dapat pagkatapos ng expression, trops.")
            Expr.Grouping(expr)
        }

        else -> {
            error(peek().token, "May expression dapat 'jan, lods.")
            throw ParseError()
        }
    }

    class ParseError : RuntimeException()
}