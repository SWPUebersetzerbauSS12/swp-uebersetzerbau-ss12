0|([1-9]+[0-9]*)		{num}

%%

\(						{return("(", "")}
\)						{return(")", "")}
\{						{return("{", "")}
\}						{return("}", "")}
=						{return("=", "")}
<						{return("<", "")}
>						{return(">", "")}
<=						{return("<=", "")}
>=						{return(">=", "")}
==						{return("==", "")}
!=						{return("!=", "")}
&&						{return("&&", "")}
\|\|					{return("||", "")}
!						{return("!", "")}
\+						{return("+", "")}
\-						{return("-", "")}
\*						{return("*", "")}
/						{return("/", "")}
;						{return(";", "")}
,						{return(",", "")}
\.						{return(".", "")}
record					{return("record", "")}
def						{return("def", "")}
if						{return("if", "")}
while					{return("while", "")}
do						{return("do", "")}
else					{return("else", "")}
print					{return("print", "")}
return					{return("return", "")}
break					{return("break", "")}
true					{return("bool", parseBoolean())}
false					{return("bool", parseBoolean())}
int						{return("basic", "int")}
real					{return("basic", "real")}
string					{return("basic", "string")}
{num}					{return("num", parseInt())}
{num}?\.{num}			{return("real", parseDouble())}
[a-z]+[a-zA-Z0-9]*		{return("id", parseString())}