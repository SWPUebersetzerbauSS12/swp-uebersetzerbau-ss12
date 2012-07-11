; ModuleID = '/home/dakn/Studium/swp_compiler/swp-uebersetzerbau-ss12/tests/de/fuberlin/optimierung/test2.c'
target datalayout = "e-p:32:32:32-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:32:64-f32:32:32-f64:32:64-v64:64:64-v128:128:128-a0:0:64-f80:32:32-n8:16:32-S128"
target triple = "i386-pc-linux-gnu"

@.str = private unnamed_addr constant [3 x i8] c"%i\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 4
  %a = alloca i32, align 4
  %b = alloca i32, align 4
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 4
  store i32 4, i32* %a, align 4
  %4 = load i32* %a, align 4
  %5 = lshr i32 %4, 1
  store i32 %5, i32* %b, align 4
  %6 = load i32* %b, align 4
  %qq = getelementptr inbounds [3 x i8]* @.str, i32 0, i32 0
  %7 = call i32 (i8*, ...)* @printf(i8* %qq, i32 %6)
  ret i32 1
}

declare i32 @printf(i8*, ...)
