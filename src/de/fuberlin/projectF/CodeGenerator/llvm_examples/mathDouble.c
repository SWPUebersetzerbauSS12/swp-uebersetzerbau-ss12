#include <stdio.h>

int main()
{
	double a = 5.5;
	double b = 3.2;
	double erg;
	
	erg = a + b;
	printf("%f + %f = %f\n", a, b, erg);
	
	erg = a - b;
	printf("%f - %f = %f\n", a, b, erg);
	
	erg = a * b;
	printf("%f * %f = %f\n", a, b, erg);
	
	erg = a / b;
	printf("%f / %f = %f\n", a, b, erg);
	
	return 0;
}

	
