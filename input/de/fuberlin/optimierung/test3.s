; ModuleID = '/home/dakn/Studium/swp_compiler/swp-uebersetzerbau-ss12/tests/de/fuberlin/optimierung/test3.c'
target datalayout = "e-p:32:32:32-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:32:64-f32:32:32-f64:32:64-v64:64:64-v128:128:128-a0:0:64-f80:32:32-n8:16:32-S128"
target triple = "i386-pc-linux-gnu"

%struct.test = type { [5 x i32], float }

@.str = private unnamed_addr constant [7 x i8] c"int:%i\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 4
  %a = alloca %struct.test, align 4
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 4
  %4 = getelementptr inbounds %struct.test* %a, i32 0, i32 0
  %5 = getelementptr inbounds [5 x i32]* %4, i32 0, i32 3
  store i32 5, i32* %5, align 4
  %6 = getelementptr inbounds %struct.test* %a, i32 0, i32 1
  store float 5.000000e+00, float* %6, align 4
  %7 = getelementptr inbounds %struct.test* %a, i32 0, i32 0
  %8 = getelementptr inbounds [5 x i32]* %7, i32 0, i32 3
  %9 = load i32* %8, align 4
  %qq = getelementptr inbounds [7 x i8]* @.str, i32 0, i32 0
  %10 = call i32 (i8*, ...)* @printf(i8* %qq, i32 %9)
  %11 = getelementptr inbounds %struct.test* %a, i32 0, i32 0
  %12 = getelementptr inbounds [5 x i32]* %11, i32 0, i32 3
  %13 = load i32* %12, align 4
  ret i32 %13
}

declare i32 @printf(i8*, ...)
