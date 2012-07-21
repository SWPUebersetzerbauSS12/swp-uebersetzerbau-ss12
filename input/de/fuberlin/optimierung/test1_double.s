; ModuleID = '/tmp/webcompile/_26199_0.bc'
target datalayout = "e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

%struct.test = type { i32, double }

@.str = private unnamed_addr constant [7 x i8] c"int:%i\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 8
  %a = alloca %struct.test, align 8
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 8
  %4 = getelementptr inbounds %struct.test* %a, i32 0, i32 0
  store i32 5, i32* %4, align 4
  %5 = getelementptr inbounds %struct.test* %a, i32 0, i32 1
  store double 5.000000e+00, double* %5, align 8
  %6 = getelementptr inbounds %struct.test* %a, i32 0, i32 0
  %7 = load i32* %6, align 4
  %8 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([7 x i8]* @.str, i32 0, i32 0), i32 %7)
  %9 = getelementptr inbounds %struct.test* %a, i32 0, i32 0
  %10 = load i32* %9, align 4
  ret i32 %10
}

declare i32 @printf(i8*, ...)
