#include <stdio.h>

int main()
{
	int a = 0x55;
	int b = 0x22;
	
	int c = a | b;
	printf("%x | %x = %x\t\n", a, b, c);
	
	c = a & b;
	printf("%x & %x = %x\t\n", a, b, c); 
	
	c = a ^ b;
	printf("%x ^ %x = %x\t\n", a, b, c); 
	
	return 0;
}
