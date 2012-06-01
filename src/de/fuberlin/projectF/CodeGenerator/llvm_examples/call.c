#include <stdio.h>

int addition(int a, int b)
{
	return a + b;
}

int main()
{
	int a = 5;
	int b = 6;
	int erg = addition(a, b);
	printf("Ergebniss von %d + %d ist: %d\n", a, b, erg);
	return 0;
}
