; ModuleID = 'twoReturns.c'
target datalayout = "e-p:32:32:32-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:32:64-f32:32:32-f64:32:64-v64:64:64-v128:128:128-a0:0:64-f80:32:32-n8:16:32-S128"
target triple = "i386-pc-linux-gnu"

@.str = private unnamed_addr constant [7 x i8] c"hallo\0A\00", align 1
@.str1 = private unnamed_addr constant [4 x i8] c"du\0A\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 4
  %a = alloca i32, align 4
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 4
  %4 = load i8*** %3, align 4
  %5 = getelementptr inbounds i8** %4, i32 1
  %6 = load i8** %5
  %7 = call i32 @atoi(i8* %6) nounwind readonly
  store i32 %7, i32* %a, align 4
  %8 = load i32* %a, align 4
  %9 = icmp slt i32 %8, 5
  br i1 %9, label %10, label %12

; <label>:10                                      ; preds = %0
  %11 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([7 x i8]* @.str, i32 0, i32 0))
  br label %14

; <label>:12                                      ; preds = %0
  %13 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([4 x i8]* @.str1, i32 0, i32 0))
  br label %14

; <label>:14                                      ; preds = %12, %10
  ret i32 0
}

declare i32 @atoi(i8*) nounwind readonly

declare i32 @printf(i8*, ...)
