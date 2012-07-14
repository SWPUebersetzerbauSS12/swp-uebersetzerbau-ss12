; ModuleID = '/tmp/webcompile/_21217_0.bc'
target datalayout = "e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

@.str = private unnamed_addr constant [4 x i8] c"%i\0A\00", align 1

define void @factorial(i32* %a) nounwind {
  %1 = alloca i32*, align 8
  store i32* %a, i32** %1, align 8
  %2 = load i32** %1, align 8
  %3 = getelementptr inbounds i32* %2, i64 3
  store i32 3, i32* %3
  ret void
}

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 8
  %a = alloca [5 x i32], align 16
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 8
  %4 = getelementptr inbounds [5 x i32]* %a, i32 0, i32 0
  call void @factorial(i32* %4)
  %5 = getelementptr inbounds [5 x i32]* %a, i32 0, i64 3
  %6 = load i32* %5, align 4
  %q = getelementptr inbounds [4 x i8]* @.str, i32 0, i32 0
  %7 = call i32 (i8*, ...)* @printf(i8* %q, i32 %6)
  ret i32 1
}

declare i32 @printf(i8*, ...)
