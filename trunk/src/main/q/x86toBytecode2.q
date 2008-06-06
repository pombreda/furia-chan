

mov A B = load(A) n store(B);


load(A)=iload A if is32bitReg(A);
store(A)=istore A if is32bitReg(A);

is32bitReg(A) = true if A = 0 || A = 1;
              = false otherwise;

eax = 0;
ebx = 1; 
ecx = 2;
edx = 3;