.section .data
str:	.ascii "Ergebniss von %d + %d ist :  %d\n\0"
.section .text
.type addition, @function
addition:
	enter $0, $0
	subl $4, %esp	#Allocation %1
	subl $4, %esp	#Allocation %2
	movl 8(%ebp), %eax	#Copy assignment
	movl %eax, -4(%ebp)	#%1%a
	movl 12(%ebp), %eax	#Copy assignment
	movl %eax, -8(%ebp)	#%2%b
	movl -4(%ebp), %eax	#Addition
	addl -8(%ebp), %eax	#%3 + %4
	movl %eax, %ebx	#Return value
	leave
	ret

.type main, @function
main:
	enter $0, $0
	subl $4, %esp	#Allocation %1
	subl $4, %esp	#Allocation %a
	subl $4, %esp	#Allocation %b
	subl $4, %esp	#Allocation %erg
	movl $0, -4(%ebp)	#Assignment %1
	movl $5, -8(%ebp)	#Assignment %a
	movl $6, -12(%ebp)	#Assignment %b
	pushl -12(%ebp)	#Parameter %3
	pushl -8(%ebp)	#Parameter %2
	call addition	#Call addition
	addl $4, %esp	#Dismiss Parameter
	addl $4, %esp	#Dismiss Parameter
	movl %ebx, -16(%ebp)	#Assignment %erg
	pushl -16(%ebp)	#Parameter %7
	pushl -12(%ebp)	#Parameter %6
	pushl -8(%ebp)	#Parameter %5
	pushl $str	#Parameter @.str
	call printf	#Call printf
	addl $4, %esp	#Dismiss Parameter
	addl $4, %esp	#Dismiss Parameter
	addl $4, %esp	#Dismiss Parameter
	addl $4, %esp	#Dismiss Parameter
	movl $0, %eax	#Return Value
	leave
	ret

.globl _start
_start:
	call main
	pushl %eax
	call exit

