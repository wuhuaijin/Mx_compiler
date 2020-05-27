grammar Mx;

//Key words
BOOL    : 'bool';
INT     : 'int';
STRING  : 'string';
VOID    : 'void';

TRUE    : 'true';
FALSE   : 'false';
NULL    : 'null';

IF      : 'if';
ELSE    : 'else';
FOR     : 'for';
WHILE   : 'while';
BREAK   : 'break';
CONTINUE: 'continue';
RETUREN : 'return';

NEW     : 'new';
CLASS   : 'class';
THIS    : 'this';

//op
MUL     : '*';
DIV     : '/';
SUB     : '-';
ADD     : '+';


//whitespace & comment
WS :                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT :            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT :       '//' ~[\r\n]*    -> channel(HIDDEN);

NUM : [1-9][0-9]*
    | '0'
    ;

ID : [a-zA-Z][a-zA-Z0-9_]*;

STR : '"' (ESC|.)*? '"';

ESC : '\\"' | '\\\\' ;

mainProg : prog;

prog : (def)+;

//prog : (classDef | funcDef | variableDef)+;

def : classDef
    | funcDef
    | varDef
    ;

classDef : CLASS ID '{' (varDef | funcDef | creatorDef)* '}' ';';

funcDef : classStat ID '(' paraList? ')' block;

varDef : classStat (ID '=' expr | ID) (',' (ID '=' expr | ID))* ';';

variableDef : classStat ID '=' expr
            | classStat ID
            ;

creatorDef
    :   ID '(' ')' block
    ;


classStat : classStat '[' ']'
          | className
          ;

className   : BOOL
            | INT
            | STRING
            | VOID
            | ID
            ;

paraList : paraDef (',' paraDef)*;

paraDef : classStat ID;

block : '{' stat* '}';

stat : varDef                                                       # defineStat
     | IF '(' expr ')' stat (ELSE stat)?                            # ifStat
     | FOR '(' init=expr? ';' cond=expr? ';' step=expr? ')' stat?    # forStat
     | WHILE '(' expr ')' stat?                                      # whileStat
     | BREAK ';'                                                    # breakStat
     | CONTINUE ';'                                                 # continueStat
     | RETUREN expr? ';'                                             # returnStat
     | ';'                                                          # noStat
     | expr? ';'                                                    # exprStat
     | block                                                        # blockStat
     ;


expr : expr op=('++' | '--')                                        # postfixExpr
     | <assoc=right> NEW creator                                    # newExpr
     | expr '(' exprList? ')'                                        # funcCallExpr
     | expr '[' expr ']'                                            # arrayExpr
     | '(' expr ')'                                                 # subExpr
     | expr '.' (ID | THIS)                                         # memberExpr
     | op=('++' | '--') expr                                        # prefixExpr
     | op=('+' | '-') expr                                          # prefixExpr
     | op='~' expr                                                  # prefixExpr
     | op='!' expr                                                  # prefixExpr
     | expr op=('*' | '/' | '%') expr                               # binaryExpr
     | expr op=('+' | '-') expr                                     # binaryExpr
     | expr op=('<<' | '>>') expr                                   # binaryExpr
     | expr op=('<'|'<='|'>'|'>=') expr                             # binaryExpr
     | expr op=('=='|'!=') expr                                     # binaryExpr
     | expr op='&' expr                                                # binaryExpr
     | expr op='^' expr                                                # binaryExpr
     | expr op='|' expr                                                # binaryExpr
     | <assoc=right>expr op='&&' expr                                  # binaryExpr
     | <assoc=right>expr op='||' expr                                  # binaryExpr
     | <assoc=right> expr op='=' expr                                  # binaryExpr
     | THIS                                                         # thisExpr
     | TRUE                                                         # constExpr
     | FALSE                                                        # constExpr
     | NUM                                                          # constExpr
     | STR                                                          # constExpr
     | NULL                                                         # constExpr
     | ID                                                           # idExpr
     ;




exprList
    :   expr (',' expr)*
    ;

creator : className (dimension* ('(' ')')? );

dimension : '[' expr? ']';