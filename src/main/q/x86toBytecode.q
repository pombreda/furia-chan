// rules used to transform assembly code instructions to java bytecode 

// this is added here so that parameters respect this :)
// mem is the memory array
//type Reg64 = const rax, rbx, rcx, rdx, rsp, rbp, rsi, rdi;
type Reg32 = const this, mem, eax, ebx, ecx, edx, esi, edi, ebp, esp;
type Reg16 = const ax, bx, cx, dx, si, di, bp, sp;
type Reg8 = const ah, al, bh,bl, ch,cl, dh,dl;
type RegFloat = const st0, st1, st2, st3, st4, st5, st6, st7;
type RegDouble = const std0;

isRegInt(A) = isReg32(A) or isReg16(A) or isReg8(A);
isReg32(A) = isint (A - eax);
isReg16(A) = isint (A - ax);
isReg8(A) = isint (A - ah);
isRegFloat(A) = isint (A - st0);


// memory datatypes.
type Mem = const memInt, memShort, memByte, memLong, memDouble, memFloat;


mov A B = addr(A) ++ value(B) ++  store(A);
add A B = value(A) ++ value(B) ++ t_add(A); // should define routines for add
sub A B = value(A) ++ value(B) ++ t_sub(A); // to handle different types.


// leaving swap to give emphasis of the instruction change
xchg A B = value(A) ++ value(B) ++ [swap] ++  c_store(B) ++ c_store(A);

// obtain an address
// Leave the arrayref and index ready for a reading or writing operation
addr([A plus C:Int]) = [aload(e( memInt )) , load(e(A)),  iconst C, iadd] 
												 if isRegInt(A) ;

addr([A minus C:Int]) = [aload(e( memInt )) , load(e(A)),  iconst C, isub]
														if isRegInt(A);
addr([A]) = [aload(e( memInt )) , iload(e(A))]
							               if isRegInt(A);
// any address is empty
addr(_) = [];

// obtain a value.
value([A]) = addr([A]) ++ [iaload] if isRegInt(A);
value(A) = addr(A) ++ [iload(e(A))] if isRegInt(A);

// load a value.
load(A)=iload(e(A)) if isRegInt(A);
load(A:RegFloat)=fload(e(A));
load(A:RegDouble)=dload(e(A));

// complete store (includes the address)
c_store(A) = addr(A) ++ store(A);

// Store a values
//8, 16, 32 regs
store(A)=[istore(e(A))] if isRegInt(A);
store([A])= [iastore] if isRegInt(A);
// float regs
store(A:RegFloat)=[fstore(e(A))];
store([A:RegFloat])=[fastore];
// double regs
store(A:RegDouble)=[dstore(e(A))];
store([A:RegDouble])=[dastore];


//add(A:Reg64) = ladd;
t_add(A:RegFloat) = [fadd];
t_add(A:RegDouble) = [dadd];
t_add(A) = [iadd] if isRegInt(A);

t_sub(A:RegFloat) = [fsub];
t_sub(A:RegDouble) = [dsub];
t_sub(_) = [isub];

t_mul(A:RegFloat) = [fmul];
t_mul(A:RegDouble) = [dmul];
t_mul(A) = [imul] if isRegInt(A);


//e(A:Reg32) = ord(A);
//e(A:Reg16) = ord(A) + #enum_from eax;
//e(A:Mem) = ord(A) + #enum_from eax + #enum_from ax;




