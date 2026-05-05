# spreadsheet-formula-compiler

##  Project Overview
This project is a complete compiler front-end for a custom Spreadsheet Formula Language, developed as part of a Computer Science Compiler course. It transforms raw spreadsheet formulas (starting with `=`) into a structured Abstract Syntax Tree (AST) using a handwritten lexical analyzer and a recursive-descent parser.

##  Key Features
- **Handwritten Scanner**: Tokenizes input without using Regular Expressions (RE) per academic requirements.
- **Recursive-Descent Parser**: Handles operator precedence (e.g., multiplication before addition) and nested expressions.
- **Supported Syntax**:
  - Cell References (e.g., `A1`, `BC10`).
  - Arithmetic Operations (`+`, `-`, `*`, `/`).
  - Function Calls (`SUM`, `MAX`, `MIN`) with multiple arguments.
  - Conditional Logic (`IF` statements).
  - Range Operations (e.g., `A1:B5`).
- **AST Visualization**: Implements the **Visitor Pattern** to traverse and display the structural output of the parsed formulas.

##  Tech Stack
- **Language**: Java
- **Build Tool**: IntelliJ IDEA IDE
- **Architecture**: Front-End (Scanner -> Parser -> AST)

##  Grammar (EBNF)
The parser is built based on the following grammar rules:
```text
Formula       = "=" , Expression
Expression    = Term , { ("+" | "-") , Term }
Term          = Factor , { ("*" | "/") , Factor }
Factor        = Number | CellRef | Range | FunctionCall | IfCondition | "(" , Expression , ")"
Range        = CellReference , ":" , CellReference 

FunctionCall = FnName , "(" , ArgumentList , ")" 
FnName       = "SUM" | "MAX" | "MIN" ;
ArgumentList = Expression , { "," , Expression } 


IfCondition  = "IF" , "(" , Comparison , "," , Expression , "," , Expression , ")" 
Comparison   = Expression , CompOp , Expression 


Op           = "+" | "-" 
HOp          = "*" | "/" 


CompOp       = ">=" | "<=" | "<>" | "=" | ">" | "<"
```
