; ModuleID = '/tmp/webcompile/_15686_0.bc'
target datalayout = "e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

@.str = private unnamed_addr constant [3 x i8] c"%i\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 8
  %a = alloca i32, align 4
  %b = alloca i32, align 4
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 8
  %4 = load i8*** %3, align 8
  %5 = getelementptr inbounds i8** %4, i64 1
  %6 = load i8** %5
  %7 = call i32 @atoi(i8* %6) nounwind readonly
  store i32 %7, i32* %a, align 4
  %8 = load i32* %a, align 4
  %9 = mul i32 2, %8
  store i32 %9, i32* %b, align 4
  %10 = load i32* %b, align 4
  %q = getelementptr inbounds [3 x i8]* @.str, i32 0, i32 0
  %11 = call i32 (i8*, ...)* @printf(i8* %q, i32 %10)
  ret i32 1
}

declare i32 @atoi(i8*) nounwind readonly

declare i32 @printf(i8*, ...)
