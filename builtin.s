	.file	"builtin.c"
	.option nopic
	.text
	.align	2
	.globl	string_length
	.type	string_length, @function
string_length:
	tail	strlen
	.size	string_length, .-string_length
	.align	2
	.globl	string_substring
	.type	string_substring, @function
string_substring:
	addi	sp,sp,-32
	sw	s0,24(sp)
	sub	s0,a2,a1
	sw	s3,12(sp)
	mv	s3,a0
	addi	a0,s0,1
	sw	ra,28(sp)
	sw	s1,20(sp)
	sw	s2,16(sp)
	mv	s2,a1
	call	malloc
	add	a5,a0,s0
	add	a1,s3,s2
	mv	a2,s0
	sb	zero,0(a5)
	mv	s1,a0
	call	memcpy
	lw	ra,28(sp)
	lw	s0,24(sp)
	lw	s2,16(sp)
	lw	s3,12(sp)
	mv	a0,s1
	lw	s1,20(sp)
	addi	sp,sp,32
	jr	ra
	.size	string_substring, .-string_substring
	.section	.rodata.str1.4,"aMS",@progbits,1
	.align	2
.LC0:
	.string	"%d"
	.text
	.align	2
	.globl	string_parseInt
	.type	string_parseInt, @function
string_parseInt:
	addi	sp,sp,-32
	lui	a1,%hi(.LC0)
	addi	a2,sp,12
	addi	a1,a1,%lo(.LC0)
	sw	ra,28(sp)
	call	__isoc99_sscanf
	lw	ra,28(sp)
	lw	a0,12(sp)
	addi	sp,sp,32
	jr	ra
	.size	string_parseInt, .-string_parseInt
	.align	2
	.globl	string_ord
	.type	string_ord, @function
string_ord:
	add	a0,a0,a1
	lbu	a0,0(a0)
	ret
	.size	string_ord, .-string_ord
	.align	2
	.globl	string_add
	.type	string_add, @function
string_add:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	sw	s1,20(sp)
	sw	s2,16(sp)
	sw	s3,12(sp)
	sw	s4,8(sp)
	mv	s2,a1
	mv	s4,a0
	call	strlen
	mv	s0,a0
	mv	a0,s2
	call	strlen
	mv	s3,a0
	add	a0,s0,a0
	addi	a0,a0,1
	call	malloc
	mv	a2,s0
	mv	a1,s4
	mv	s1,a0
	call	memcpy
	add	a0,s1,s0
	addi	a2,s3,1
	mv	a1,s2
	call	memcpy
	lw	ra,28(sp)
	lw	s0,24(sp)
	lw	s2,16(sp)
	lw	s3,12(sp)
	lw	s4,8(sp)
	mv	a0,s1
	lw	s1,20(sp)
	addi	sp,sp,32
	jr	ra
	.size	string_add, .-string_add
	.align	2
	.globl	string_eq
	.type	string_eq, @function
string_eq:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	seqz	a0,a0
	addi	sp,sp,16
	jr	ra
	.size	string_eq, .-string_eq
	.align	2
	.globl	string_neq
	.type	string_neq, @function
string_neq:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	snez	a0,a0
	addi	sp,sp,16
	jr	ra
	.size	string_neq, .-string_neq
	.align	2
	.globl	string_lt
	.type	string_lt, @function
string_lt:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	srli	a0,a0,31
	addi	sp,sp,16
	jr	ra
	.size	string_lt, .-string_lt
	.align	2
	.globl	string_le
	.type	string_le, @function
string_le:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	slti	a0,a0,1
	addi	sp,sp,16
	jr	ra
	.size	string_le, .-string_le
	.align	2
	.globl	string_gt
	.type	string_gt, @function
string_gt:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	sgt	a0,a0,zero
	addi	sp,sp,16
	jr	ra
	.size	string_gt, .-string_gt
	.align	2
	.globl	string_ge
	.type	string_ge, @function
string_ge:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	not	a0,a0
	srli	a0,a0,31
	addi	sp,sp,16
	jr	ra
	.size	string_ge, .-string_ge
	.section	.rodata.str1.4
	.align	2
.LC1:
	.string	"%s"
	.text
	.align	2
	.globl	print
	.type	print, @function
print:
	mv	a1,a0
	lui	a0,%hi(.LC1)
	addi	a0,a0,%lo(.LC1)
	tail	printf
	.size	print, .-print
	.align	2
	.globl	println
	.type	println, @function
println:
	tail	puts
	.size	println, .-println
	.align	2
	.globl	printInt
	.type	printInt, @function
printInt:
	mv	a1,a0
	lui	a0,%hi(.LC0)
	addi	a0,a0,%lo(.LC0)
	tail	printf
	.size	printInt, .-printInt
	.section	.rodata.str1.4
	.align	2
.LC2:
	.string	"%d\n"
	.text
	.align	2
	.globl	printlnInt
	.type	printlnInt, @function
printlnInt:
	mv	a1,a0
	lui	a0,%hi(.LC2)
	addi	a0,a0,%lo(.LC2)
	tail	printf
	.size	printlnInt, .-printlnInt
	.align	2
	.globl	getString
	.type	getString, @function
getString:
	addi	sp,sp,-16
	li	a0,1200
	sw	ra,12(sp)
	sw	s0,8(sp)
	call	malloc
	mv	s0,a0
	mv	a1,a0
	lui	a0,%hi(.LC1)
	addi	a0,a0,%lo(.LC1)
	call	__isoc99_scanf
	lw	ra,12(sp)
	mv	a0,s0
	lw	s0,8(sp)
	addi	sp,sp,16
	jr	ra
	.size	getString, .-getString
	.align	2
	.globl	getInt
	.type	getInt, @function
getInt:
	addi	sp,sp,-32
	lui	a0,%hi(.LC0)
	addi	a1,sp,12
	addi	a0,a0,%lo(.LC0)
	sw	ra,28(sp)
	call	__isoc99_scanf
	lw	ra,28(sp)
	lw	a0,12(sp)
	addi	sp,sp,32
	jr	ra
	.size	getInt, .-getInt
	.align	2
	.globl	toString
	.type	toString, @function
toString:
	addi	sp,sp,-16
	sw	s1,4(sp)
	mv	s1,a0
	li	a0,1200
	sw	ra,12(sp)
	sw	s0,8(sp)
	call	malloc
	lui	a1,%hi(.LC0)
	mv	a2,s1
	addi	a1,a1,%lo(.LC0)
	mv	s0,a0
	call	sprintf
	lw	ra,12(sp)
	mv	a0,s0
	lw	s0,8(sp)
	lw	s1,4(sp)
	addi	sp,sp,16
	jr	ra
	.size	toString, .-toString
	.ident	"GCC: (GNU) 9.2.0"
	.section	.note.GNU-stack,"",@progbits
