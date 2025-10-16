package example.lexicalscanner.utils.printer

import example.lexicalscanner.syntax.Expr
import example.lexicalscanner.syntax.Stmt

class AstPrinter {

    /** Prints the full statement tree to console once (top-level only). */
    fun printStmt(stmt: Stmt?) {
        val result = stmt?.let { formatStmt(it) } ?: "null"
        println(result)
    }

    /** Returns the AST as a formatted string — used internally. */
    private fun formatStmt(stmt: Stmt?): String {
        if (stmt == null) return "null"

        return when (stmt) {
            is Stmt.Program ->
                "(${formatStmt(stmt.root)})"

            is Stmt.Block -> {
                val first = stmt.first?.let { formatStmt(it) } ?: "null"
                val next = stmt.next?.let { formatStmt(it) } ?: ""
                "(block $first $next)".trim()
            }

            is Stmt.ExpressionStmt ->
                formatExpr(stmt.expression)

            is Stmt.VariableDecl ->
                "(var ${stmt.name.lexeme} = ${formatExpr(stmt.initializer)})"

            is Stmt.FunctionStmt -> {
                val param = stmt.param?.lexeme ?: "none"
                val nextParam = stmt.nextParam?.let { formatStmt(it) } ?: ""
                val body = formatStmt(stmt.body)
                "(def ${stmt.name.lexeme} ($param $nextParam) $body)"
            }

            is Stmt.IfStmt ->
                "(if ${formatExpr(stmt.condition)} then ${formatStmt(stmt.thenBranch)} else ${formatStmt(stmt.elseBranch)})"

            is Stmt.WhileStmt ->
                "(while ${formatExpr(stmt.condition)} ${formatStmt(stmt.body)})"

            is Stmt.ForStmt ->
                "(for ${stmt.variable.lexeme} in ${formatExpr(stmt.iterable)} ${formatStmt(stmt.body)})"

            is Stmt.ReturnStmt ->
                "(return ${formatExpr(stmt.value)})"

            else -> "(unknown-stmt)"
        }
    }

    /** Formats expressions (not printed recursively). */
    private fun formatExpr(expr: Expr?): String {
        if (expr == null) return "null"

        return when (expr) {
            is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
            is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
            is Expr.Grouping -> parenthesize("group", expr.expression)
            is Expr.Literal -> expr.value?.toString() ?: "null"
            is Expr.Variable -> expr.name.lexeme
            else -> "error"
        }
    }

    /** Helper to format expressions with parentheses. */
    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ").append(formatExpr(expr))
        }
        builder.append(")")
        return builder.toString()
    }
}