#include <stdio.h>

int main()
{
	int a = 12;
	printf("bevor: %d\n", a);
	
	a = a << 1;
	printf("left shift: %d\n", a);
	
	a = 12;
	
	a = a >> 1;
	printf("right shift: %d\n", a);
	
	return 0;
}
