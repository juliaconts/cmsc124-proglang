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

        val tree = parser.parse()
        printer.printStmt(tree)
    }
}
//        try {
//            parser.parse()
//            println("Walang syntax error, pre.")66
//        } catch (e: Exception) {
//            println("Syntax error: ${e.message}")
//        }

//        val printer = AstPrinter()
//        println(printer.print(expr))

//        for (token in tokens) {
//            println("Token(type = ${token.type}, lexeme = ${token.lexeme}, literal = ${token.literal}, line = ${token.line})")
//        }