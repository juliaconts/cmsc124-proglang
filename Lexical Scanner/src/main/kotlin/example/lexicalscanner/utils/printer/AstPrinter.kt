package example.lexicalscanner.utils.printer

import example.lexicalscanner.syntax.Expr
import example.lexicalscanner.syntax.Stmt

class AstPrinter {
    fun print(expr: Expr?): String {
        if (expr == null) return "nil"

        return when (expr) {
            is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
            is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
            is Expr.Grouping -> parenthesize("group", expr.expression)
            is Expr.Literal -> expr.value?.toString() ?: "nil"
            is Expr.Variable -> expr.name.lexeme
            else -> "nil"
        }
    }

    fun printStmt(stmt: Stmt?): String {
        return when (stmt) {
            is Stmt.ExpressionStmt -> print(stmt.expression)
            is Stmt.VariableDecl -> "(var ${stmt.name.lexeme} = ${print(stmt.initializer)})"
            is Stmt.Block -> stmt.statements.joinToString(" ") { printStmt(it) }
            is Stmt.IfStmt -> "(if ${print(stmt.condition)} then ${printStmt(stmt.thenBranch)} else ${printStmt(stmt.elseBranch)})"
            is Stmt.WhileStmt -> "(while ${print(stmt.condition)} ${printStmt(stmt.body)})"
            is Stmt.ForStmt -> "(for ${stmt.variable.lexeme} sa ${print(stmt.iterable)} ${(stmt.body as? Stmt.Block)?.statements?.joinToString(" ") { b -> printStmt(b) } ?: printStmt(stmt.body)})"
            is Stmt.ReturnStmt -> "(return ${print(stmt.value)})"
            is Stmt.FunctionStmt -> "(def ${stmt.name.lexeme} (${stmt.params.joinToString(" ") { it.lexeme }}) ${stmt.body.joinToString(" ") { printStmt(it) }})"
            else -> "nil"
        }
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs){
            builder.append(" ").append(print(expr))
        }
        builder.append(")")
        return builder.toString()
    }
}