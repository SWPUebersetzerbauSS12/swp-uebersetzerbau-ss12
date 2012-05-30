.section .data
str:	.ascii "Hello World\n\00"
.section .text
.type main, @function
main:
	enter $0, $0
	subl $4, %esp	#Allocation %1
	movl $0, -4(%ebp)	#Assignment %1
	pushl $str	#
	call printf	#Call printf
	movl $0, %eax	#Return Value
	leave
	ret

.globl _start
_start:
	call main
	pushl %eax
	call exit

