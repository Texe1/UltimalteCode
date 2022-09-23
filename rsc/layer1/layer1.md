#Layer 1

___

##1. Tokens

###1.1 Static Tokens
>Static Tokens are tokens that don't have different String representations

####1.1.1 Operators
    
    +   Addition 
    -   Subtraction
    *   Multiplication
    /   Division
    %   Modulo
    &   Bitwise AND
    |   Bitwise OR
    ^   Bitwise XOR
    >>  right bit shift
    <<  left bit shift
___
    @   gets the value at the specified address
    ~   gets the address of a value
___
    =       Assignment
    <op>=   using operator in place on left operand
___
####1.1.2 Logical Operators
    !   logical NOT
    &&  logical AND
    ||  logical OR
    ^^  logical XOR
___
    ==  equality check
    !=  inequality check
    >   'more than' operator
    <   'less than' operator
    >=  'more or equal' operator
    <=  'less or equal' operator
    |>  unsigned 'more'
    |<  unsigned 'less'
___
####1.1.3 Keywords
    func    declares a function
    import  declares extern functions, usually from dynamic libraries
    call    calls a function
___
    if      executes the following code if and only if the condition is met
    else    executes if previous if statement did not
___
    byte    8-bit data type
    char    alternative to byte (recommended against)
    word    16-bit data type
    short   alternative to word (recommended against)
    int     32-bit data type
    long    64-bit data type
___
    arg     defines a variable to be derived from the argument bytes of a function
___

####1.1.4 Others
    :       declares an argument to be only the following number of bits (8, 16, 32 or 64)
