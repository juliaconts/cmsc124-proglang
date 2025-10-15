package example.lexicalscanner

import example.lexicalscanner.syntax.Parser
import example.lexicalscanner.utils.printer.AstPrinter
import example.lexicalscanner.utils.*


fun main() {
    val printer = AstPrinter()
    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        if (line.trim() == "exit") break
        if (line.isBlank()) continue

        val scanner = Scanner(line)
        val tokens = scanner.scanInput()
        val parser = Parser(tokens)

        val keywordFactory = KeywordFactory("")

        val containsKeyword = keywordFactory.keywords.keys.any { kw: String ->
            Regex("\\b$kw\\b").containsMatchIn(line)
        }

        if (containsKeyword || line.trimEnd().endsWith(";")) {
            val stmts = parser.parseStmt()
            for (stmt in stmts) {
                println(printer.printStmt(stmt))
            }
        } else {
            val expr = try {
                parser.parseExpr()
            } catch (e: Parser.ParseError) {
                continue
            }

            if (expr != null) {
                println(printer.print(expr))
            }
        }
    }
}
//        try {
//            parser.parse()
//            println("Walang syntax error, pre.")
//        } catch (e: Exception) {
//            println("Syntax error: ${e.message}")
//        }

//        val printer = AstPrinter()
//        println(printer.print(expr))

//        for (token in tokens) {
//            println("Token(type = ${token.type}, lexeme = ${token.lexeme}, literal = ${token.literal}, line = ${token.line})")
//        }