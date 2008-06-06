// Rules used to transform assembly code instructions to java bytecode 

type Reg32 = const eax, ebx, ecx, edx;
type Reg16 = const ax, bx, cx, dx;



mov [A] B = addr(A) ++ [  n , store(B)];

mov A B =  [load(A),  n , store(B)];

//addr(A+C) = []

load(A:Reg32)=iload(e(A));
store(A:Reg32)=istore(e(A));


e(A:Reg32) = ord(A);
e(A:Reg16) = ord(A) + #enum_from eax;




