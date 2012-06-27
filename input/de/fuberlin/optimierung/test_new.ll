declare i32 @puts(i8*) nounwind
define i32 @main() nounwind { 
%i = alloca i32
%j = alloca i32
%s1 = alloca i8*
%s2 = alloca i8*
store i32 1, i32* %i
store i32 2, i32* %j
%1 = alloca [6 x i8]
store [6 x i8] c"Hallo\00", [6 x i8]* %1
%2 = getelementptr [6 x i8]* %1, i8 0, i8 0 
store i8* %2, i8** %s1
%3 = alloca [5 x i8]
store [5 x i8] c"Welt\00", [5 x i8]* %3
%4 = getelementptr [5 x i8]* %3, i8 0, i8 0 
store i8* %4, i8** %s2
%5 = load i32* %i
%6 = load i32* %j
%7 = icmp sle i32 %5, %6
br i1 %7, label %8, label %11

; <label>:8
%9 = load i8** %s1
%10 = tail call i32 (i8*)* @puts(i8* %9)
br label %14

; <label>:11
%12 = load i8** %s2
%13 = tail call i32 (i8*)* @puts(i8* %12)
br label %14

; <label>:14
ret i32 0
}

