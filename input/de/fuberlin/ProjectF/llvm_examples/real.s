	.file	"real.c"
	.section	.rodata
.LC2:
	.string	"%f + %f = %f\n"
	.text
	.globl	main
	.type	main, @function
main:
.LFB0:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$32, %rsp
	movabsq	$4616752568008179712, %rax
	movq	%rax, -24(%rbp)
	movabsq	$4615964438073389875, %rax
	movq	%rax, -16(%rbp)
	movsd	-24(%rbp), %xmm0
	addsd	-16(%rbp), %xmm0
	movsd	%xmm0, -8(%rbp)
	movl	$.LC2, %eax
	movsd	-8(%rbp), %xmm2
	movsd	-16(%rbp), %xmm1
	movsd	-24(%rbp), %xmm0
	movq	%rax, %rdi
	movl	$3, %eax
	call	printf
	movl	$0, %eax
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE0:
	.size	main, .-main
	.ident	"GCC: (Ubuntu/Linaro 4.6.1-9ubuntu3) 4.6.1"
	.section	.note.GNU-stack,"",@progbits
