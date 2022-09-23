
// this is an import
import KERNEL32, USER32 {
	printf(64) => 32
	GetCursorPos(64) => 32
}

// this is a function
func main(128) => 64 {
	long arg x
	int arg y
	short arg z
	char arg a
	byte arg b
	
	b *= 0n23;
	
	call printf(x:64)
	
	long r = @x + y * (z - a) / b
	if(r > 0v54){
	    return r: 64
	} else {
	    return 0v54: 64
    }
}