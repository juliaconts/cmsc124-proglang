package example.lexicalscanner.syntax

import example.lexicalscanner.utils.Token

interface Expr {
    data class Literal(val value: Any): Expr
    data class Unary(val operator: Token, val right: Expr): Expr
    data class Binary(val left: Expr, val operator: Token, val right: Expr): Expr
    data class Grouping(val expression: Expr): Expr
    data class Variable(val name: Token): Expr
}

interface Stmt {
    data class ExpressionStmt(val expression: Expr): Stmt
    data class VariableDecl(val name: Token, val initializer: Expr?): Stmt
    data class Block(val statements: List<Stmt>): Stmt
    data class IfStmt(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?): Stmt
    data class WhileStmt(val condition: Expr, val body: Stmt): Stmt
    data class ReturnStmt(val keyword: Token, val value: Expr?): Stmt
}