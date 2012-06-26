0|([1-9]+[0-9]*)		{num}

%%

\(						{return("BRACKET", "(")}
\)						{return("BRACKET", ")")}
\{						{return("BRACKET", "{")}
\}						{return("BRACKET", "}")}
\=						{return("ASSIGNEMENT", "=")}
<						{return("OP", "L")}
>						{return("OP", "G")}
<=						{return("OP", "LE")}
>=						{return("OP", "GE")}
==						{return("OP", "EQ")}
!=						{return("OP", "NEQ")}
\+						{return("OP", "PLUS")}
\-						{return("OP", "MINUS")}
\*						{return("OP", "MUL")}
/						{return("OP", "DIVIDE")}
;						{return("KEYWORD", "SEMIKOLON")}
basic					{return("KEYWORD", "BASIC")}
record					{return("KEYWORD", "RECORD")}
def						{return("KEYWORD", "DEF")}
if						{return("KEYWORD", "IF")}
while					{return("KEYWORD", "WHILE")}
do						{return("KEYWORD", "DO")}
else					{return("KEYWORD", "ELSE")}
print					{return("KEYWORD", "PRINT")}
return					{return("KEYWORD", "RETURN")}
break					{return("KEYWORD", "BREAK")}
true					{return("KEYWORD", "TRUE")}
false					{return("KEYWORD", "FALSE")}
{num}					{return("NUM", parseInt())}
{num}?\.{num}			{return("RAT", parseDouble())}
[a-z]+[a-zA-Z0-9]*		{return("ID", parseString())}