// rules used to transform assembly code instructions to java bytecode 

// this is added here so that parameters respect this :)
// mem is the memory array
type Reg32 = const this, mem, eax, ebx, ecx, edx;
type Reg16 = const ax, bx, cx, dx;

// memory datatypes.
type Mem = const memInt, memShort, memByte, memLong, memDouble, memFloat;


mov A B = addr(A) ++ value(B) ++  [store(A)];
add A B = value(A) ++ value(B) ++ [iadd]; // should define routines for add
sub A B = value(A) ++ value(B) ++ [isub]; // to handle different types.


// obtain an address
// Leave the arrayref and index ready for a reading or writing operation
addr([A:Reg32 plus C]) = [aload(e( memInt )) , iload(e(A)),  iconst C, iadd];
addr([A:Reg32 minus C]) = [aload(e( memInt )) , iload(e(A)),  iconst C, isub];
addr(A:Reg32) = [];
addr([A:Reg32]) = [aload(e( memInt )) , iload(e(A))];

// obtain a value.
value([A:Reg32]) = addr([A]) ++ [iaload];
value(A:Reg32) = addr(A) ++ [iload(e(A))];

load(A:Reg32)=iload(e(A));
store(A:Reg32)=istore(e(A));
store([A])=iastore;

//e(A:Reg32) = ord(A);
//e(A:Reg16) = ord(A) + #enum_from eax;
//e(A:Mem) = ord(A) + #enum_from eax + #enum_from ax;




