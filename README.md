[Your Programming Language Name]

Creators
Julo Bretaña and Julia Louise Contreras

Language Overview
[Provide a brief description of your programming language - what it's designed for, its main characteristics]
readability, less words need to by typed (???)

Keywords
[List all reserved words that cannot be used as identifiers - include the keyword and a brief description of its purpose]
variables and functions: "var" defines a variable, "def" defines a function, "return" returns a value

variable types: "int" for integers, "float" for floats, "char" for single characters, "String" for strings 

control flow: "if" for conditional branching, "else" for alternate branches, "while" for looping while condition is true, 
              "for" for iterated loops

logical values: "true" or "false" - boolean values, "null" - null values

logical operators: "and" - logical AND, "or" - logical OR 

Operators
[List all operators organized by category (arithmetic, comparison, logical, assignment, etc.)]
arithmetic: + for addition, - for subtraction, * for multiplication, / for division

comparison: == for equality, != for not equal, < for less than, <= for less than or equal to, > for greater than, >= for greater than or equal to
!! comparison is not allowed for different types

logical: ! for not, "and" for and, "or" for or

assignment: =

Literals
[Describe the format and syntax for each type of literal value (e.g., numbers, strings, characters, etc.) your language supports]
numerical and character literals will be defined by the "var" keyword, followed by their variable type, then variable name, then the assignment operator "=", ex.
var int foo = 10, var float bar = 12.34, var char coo = c

String literals will be defined by the "var" keyword, followed by their variable type, then variable name, then the assignment operator "=",
then the string enclosed in quotation marks, ex.
String vim = "Abcde"

default values are given to numerical literals if no values are assigned, ex.
var int foo, var float bar
foo = 0, bar = 0.0


Identifiers
[Define the rules for valid identifiers (variable names, function names, etc.) and whether they are case-sensitive]
identifiers cannot begin with a number
identifiers are case-sensitive, ex. fooBar != foobar


Comments
[Describe the syntax for comments and whether nested comments are supported]
single-line comments will be signified by // at the start and will end once the line ends
group comments will be signified by /* at the start and will end at */
nested comments will not be supported

Syntax Style
[Describe whether whitespace is significant, how statements are terminated, and what delimiters are used for blocks and grouping]

Sample Code
[Provide a few examples of valid code in your language to demonstrate the syntax and features]

Design Rationale
[Explain the reasoning behind your design choices]
