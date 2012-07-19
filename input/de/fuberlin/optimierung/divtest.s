; ModuleID = 'divtest.c'
target datalayout = "e-p:32:32:32-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:32:64-f32:32:32-f64:32:64-v64:64:64-v128:128:128-a0:0:64-f80:32:32-n8:16:32-S128"
target triple = "i386-pc-linux-gnu"

@.str = private unnamed_addr constant [10 x i8] c"hallo:%i\0A\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 4
  %a = alloca i32, align 4
  %b = alloca i32, align 4
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 4
  store i32 6, i32* %a, align 4
  %4 = load i32* %a, align 4
  %5 = udiv i32 %4, 4
  store i32 %5, i32* %b, align 4
  %6 = load i32* %b, align 4
  %7 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([10 x i8]* @.str, i32 0, i32 0), i32 %6)
  %8 = load i32* %b, align 4
  ret i32 %8
}

declare i32 @printf(i8*, ...)
