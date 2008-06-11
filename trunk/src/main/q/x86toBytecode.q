// rules used to transform assembly code instructions to java bytecode 
// this is added here so that parameters respect this :)
// mem is the memory array
//type Reg64 = const rax, rbx, rcx, rdx, rsp, rbp, rsi, rdi;
// we include flags here so that we can emulate instructions that access flags.
type Reg32 = const this, mem, eax, ebx, ecx, edx, esi, edi, ebp, esp, f_carry, f_complement_carry, f_direction, f_interrupt;
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

// primitive arithmetic operations used for pointers or
// arithmetic operations on bytecode
xaddAux A B = xaddr([A]) ++ xaddr([B]) ++ t_add(A);
xsubAux  A B = xaddr([A]) ++ xaddr([B]) ++ t_sub(A);
xmulAux A B = xaddr([A]) ++ xaddr([B]) ++ t_mul(A);
xdivAux A B = xaddr([A]) ++ xaddr([B]) ++ t_div(A);

// to avoid clashes we prepend "x" to all the instructions.
xmov A B = addr(A) ++ value(B) ++  store(A);
// A = A + B
xadd A B = addr(A) ++ value(A) ++ value(B) ++ t_add(A) ++  store(A); 
// A = A - B
xsub A B = addr(A) ++ value(A) ++ value(B) ++ t_sub(A) ++ store(A); // to handle different types.
// add with carry
xadc A B = xadd A B;
// sub with borrow
xsbb A B = xsub A B;
// division (unsigned)
xdiv B = [xload(selectRegister(B))] ++ value(B) ++ t_div(B) ++ store(selectRegister(B));
// signed integer divide
xidiv B = xdiv(B);
// multiplication unsigned
xmul B = [xload(selectRegister(B))] ++ value(B) ++ t_mul(B) ++ store(selectRegister(B));
// multiplication signed
ximul B = xmul B;
// increment
xinc A = xadd A 1;
// decrement
xdec A = xadd A (-1) ;
// comparison
xcmp A B = value(A) ++ value(B) ++ t_cmp(A);
// shift arithmetic left
xsal A B = value(A) ++ value(B) ++ t_shl(A);
// shift arithmetic right
xsar A B = value(A) ++ value(B) ++ t_shr(A);
// rotate left through carry
xrcl A B = xsal A B;
// rotate right through carry
xrcr A B = xsar A B;
// rotate left
xrol A B = xsal A B;
// rotate right
xror A B = xsar A B;
// Logical left
xshl A B = xsal A B;
// Logical left
xshr A B = xsar A B;

xlea A B = addr(A) ++ addr(B) ++ store(A);

xint A = invokestatic A;

xcall A = invokestatic A;


// Logical operators
// negate
xneg A = addr(A) ++  value(0) ++ value(A) ++ t_sub(A) ++ store(A);
// And
xand A B = addr(A) ++ value(A) ++ value(B) ++ t_and(A) ++ store(A);
// Or
xor A B = addr(A) ++ value(A) ++ value(B) ++ t_or(A) ++ store(A);
xxor A B = addr(A) ++ value(A) ++ value(B) ++ t_xor(A) ++ store(A);

// leaving swap to give emphasis of the instruction change
xxchg A B = value(A) ++ value(B) ++ [swap] ++  c_store(B) ++ c_store(A);

// set carry
xstc = value(1) ++ c_store(f_carry);
// clear carry
xclc = value(0) ++ c_store(f_carry);
// set direction
xstd = value(1) ++ c_store(f_direction);
// clear direction
xcld = value(0) ++ c_store(f_direction);
// set interrupt
xsti = value(1) ++ c_store(f_interrupt);
// clear interrupt
xsti = value(0) ++ c_store(f_interrupt);
// push the value into the stack.
xpush A = addr(A) ++  [xload(A)];
// pop the value from the stack into A
xpop A = c_store(A);


// push all flags
xpushf = xpush f_carry ++ xpush f_direction ++ xpush f_interrupt;
// pusha, push all registers
xpusha = xpush eax ++  xpush ebx ++ xpush ecx ++ xpush edx ++ xpush esi ++ xpush edi ++ xpush ebp ++ xpush esp ++ xpush f_carry ++ xpush f_complement_carry ++ xpush f_direction ++ xpush f_interrupt;
// pop all registers
xpopa = xpop f_interrupt ++ xpop f_direction ++ xpop f_complement_carry ++  xpop f_carry ++  xpop esp ++  xpop ebp ++  xpop edi ++  xpop esi ++  xpop edx ++  xpop ecx ++  xpop ebx ++  xpop eax;
// pop all flags
xpopf = xpop f_interrupt ++ xpop f_direction ++ xpop f_carry;



//  *********************************************************************
// Low lever instructions

// obtain an address
// Leave the arrayref and index ready for a reading or writing operation
// addr([A plus C:Int]) = [aload(e( memInt )) , xload(A),  iconst C, iadd] 
// 												 if isRegInt(A) ;

// addr([A minus C:Int]) = [aload(e( memInt )) , xload(A),  iconst C, isub]
// 														if isRegInt(A);

xaddr(A:Int) = value(A);
xaddr([A:Int]) = value(A);

xaddr([A]) = [ xload(A) ]
							               if isRegInt(A);
xaddr([A]) =  A  if  islist A;
xaddr(A) =  A  if  islist A;
// any address is empty
xaddr(_) = [];


addr([A]) = [aload(e( memInt )) , xload(A) ]
							               if isRegInt(A);
addr([A]) = [aload(e( memInt ))] ++  A  if  islist A;
addr(A) = [aload(e( memInt ))] ++  A  if  islist A;
// any address is empty
addr(_) = [];


// process adress transformations
A suma B = xaddAux A B;

A resta B = xsubAux A B;

A multi B = xmulAux A B;

A divi B = xdivAux A B;

// obtain a value.
value(A:Int) = [iconst A];
value(A) = addr(A) ++ [ xload(A) ] if isRegInt(A);
value([A]) = addr([A]) ++ [iaload] if isRegInt(A);

value([A]) = addr(A) ++ [iaload] if islist(A);
value(A) = addr(A) ++ [iaload] if islist(A);


// load a value.
xload(A) = iload(e(A)) if isRegInt(A);
xload(A:RegFloat)= fload(e(A));
xload(A:RegDouble) = dload(e(A));

// complete store (includes the address)
c_store(A) = addr(A) ++ store(A);

// Store a value
//8, 16, 32 regs
store(A)=[istore(e(A))] if isRegInt(A);
store([A])= [iastore] if isRegInt(A);
// complex register (since it was already processed, only store the object)
store([A])= [iastore] if islist(A);

// float regs
store(A:RegFloat)=[fstore(e(A))];
store([A:RegFloat])=[fastore];
// double regs
store(A:RegDouble)=[dstore(e(A))];
store([A:RegDouble])=[dastore];


// Addition
t_add(A:RegFloat) = [fadd];
t_add(A:RegDouble) = [dadd];
t_add(A) = [iadd]; // if isRegInt(A);
// Division
t_div(A:RegFloat) = [fdiv];
t_div(A:RegDouble) = [ddiv];
t_div(A) = [idiv]; // if isRegInt(A);
// Substraction
t_sub(A:RegFloat) = [fsub];
t_sub(A:RegDouble) = [dsub];
t_sub(_) = [isub];
// Multiplication
t_mul(A:RegFloat) = [fmul];
t_mul(A:RegDouble) = [dmul];
t_mul(A) = [imul]; // if isRegInt(A);

// Logical And
t_and(A) = [iand];
// Logical Or
t_or(A) = [ior]; 
// Logical xor
t_xor(A) = [xor];

t_cmp(A:RegFloat) = [fcmp];
t_cmp(A:RegDouble) = [dcmp];
t_cmp(A) = [icmp]; // if isRegInt(A);

// shift left
t_shl(A) = [ishl] if isRegInt(A);

// shift right
t_shr(A) = [ishl] if isRegInt(A);



// used to select the default register
// for some operations for a given type of register.
selectRegister(B:Reg32) = eax;
selectRegister(B:Reg16) = ax;
selectRegister(B:Reg8) = ah;

//e(A:Reg32) = ord(A);
//e(A:Reg16) = ord(A) + #enum_from eax;
//e(A:Mem) = ord(A) + #enum_from eax + #enum_from ax;



test STR A B = puts (STR ++ "...OK\n") if A == B;
             = puts (STR ++ "...FAILED\n") || fail otherwise;


tests 
= test "mov 1"
  (xmov eax ebx)
  [iload (e ebx), istore (e eax)]
||
  test "mov 2"
  (xmov [eax suma 2 multi 3 resta 1 divi 5] [ebx suma 3])
  [aload (e memInt),iload (e eax),iconst 2,iadd,iconst 3,imul,iconst 1,isub,iconst 5,idiv,aload (e memInt),iload (e ebx),iconst 3,iadd,iaload,iastore]
||
  test "add 1"
(xadd [eax suma 3] 4)
[aload (e memInt),iload (e eax),iconst 3,iadd,aload (e memInt),iload (e eax),iconst 3,iadd,iaload,iconst 4,iadd,iastore]
||
  test "lea 1"
(xlea [eax] [edx suma eax resta 0x30])
[aload (e memInt),iload (e eax),aload (e memInt),iload (e edx),iload (e eax),iadd,iconst 48,isub,iastore];
// xlea eax [edx suma eax resta 0x30];