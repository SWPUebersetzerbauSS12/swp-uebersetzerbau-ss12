0|([1-9]+[0-9]*)		{num}

%%

\(						{return("(", "")}
\)						{return(")", "")}
\{						{return("{", "")}
\}						{return("}", "")}
\[						{return("[", "")}
\]						{return("]", "")}
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
(0|([1-9]+[0-9]*))?\.(([0-9])[0-9]*)		{return("real", parseDouble())}
(0|([1-9]+[0-9]*))					{return("num", parseInt())}
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
true					{return("boolean", parseBoolean())}
false					{return("boolean", parseBoolean())}
int						{return("basic", "int")}
real					{return("basic", "real")}
string					{return("basic", "string")}
bool					{return("basic", "bool")}
\"[\w\sßäöü]*\"			{return("string", parseString())}
[a-z]+[a-zA-Z0-9]*		{return("id", parseString())}