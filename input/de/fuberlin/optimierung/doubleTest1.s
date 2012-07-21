@.str = private unnamed_addr constant [15 x i8] c"a kleiner: %f\0A\00", align 1
@.str1 = private unnamed_addr constant [15 x i8] c"b kleiner: %f\0A\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i8**, align 4
  %a = alloca double, align 8
  %b = alloca double, align 8
  store i32 0, i32* %1
  store i32 %argc, i32* %2, align 4
  store i8** %argv, i8*** %3, align 4
  store double 5.000000e-01, double* %b, align 8
  store double 7.700000e+00, double* %a, align 8
  %4 = load double* %a, align 8
  %5 = load double* %b, align 8
  %6 = fcmp olt double %4, %5
  br i1 %6, label %7, label %10

; <label>:7                                       ; preds = %0
  %8 = load double* %a, align 8
  %9 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([15 x i8]* @.str, i32 0, i32 0), double %8)
  br label %13

; <label>:10                                      ; preds = %0
  %11 = load double* %b, align 8
  %12 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([15 x i8]* @.str1, i32 0, i32 0), double %11)
  br label %14

; <label>:13                                      ; preds = %10, %7
  ret i32 0
  
  ret i32 1
}

declare i32 @printf(i8*, ...)
