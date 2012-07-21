@.str = private unnamed_addr constant [15 x i8] c"a kleiner: %f\0A\00", align 1
@.str1 = private unnamed_addr constant [15 x i8] c"b kleiner: %f\0A\00", align 1

define i32 @main(i32 %argc, i8** %argv) nounwind {
	%1 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([15 x i8]* @.str1, i32 0, i32 0), double 5.000000e-01)  
	ret i32 0  
}


declare i32 @printf(i8*, ...)
