// PUSH constant 10
@10
D=A
@SP
A=M
M=D
@SP
M=M+1
// POP local 0
@LCL
D=M
@0
A=D+A
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// PUSH constant 21
@21
D=A
@SP
A=M
M=D
@SP
M=M+1
// PUSH constant 22
@22
D=A
@SP
A=M
M=D
@SP
M=M+1
// POP argument 2
@ARG
D=M
@2
A=D+A
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// POP argument 1
@ARG
D=M
@1
A=D+A
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// PUSH constant 36
@36
D=A
@SP
A=M
M=D
@SP
M=M+1
// POP this 6
@THIS
D=M
@6
A=D+A
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// PUSH constant 42
@42
D=A
@SP
A=M
M=D
@SP
M=M+1
// PUSH constant 45
@45
D=A
@SP
A=M
M=D
@SP
M=M+1
// POP that 5
@THAT
D=M
@5
A=D+A
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// POP that 2
@THAT
D=M
@2
A=D+A
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// PUSH constant 510
@510
D=A
@SP
A=M
M=D
@SP
M=M+1
// POP temp 6
@R511
D=A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D
// PUSH local 0
@LCL
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// PUSH that 5
@THAT
D=M
@5
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
@SP
A=M
M=D+M
@SP
M=M+1

// PUSH argument 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
@SP
A=M
M=M-D
@SP
M=M+1

// PUSH this 6
@THIS
D=M
@6
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// PUSH this 6
@THIS
D=M
@6
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
@SP
A=M
M=D+M
@SP
M=M+1

// sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
@SP
A=M
M=M-D
@SP
M=M+1

// PUSH temp 6
@R511
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
@SP
A=M
M=D+M
@SP
M=M+1

