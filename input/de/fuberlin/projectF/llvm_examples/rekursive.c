#include <stdio.h>

int rekursive(int a);

int main()
{
	int a = 10;
	int erg;
	
	erg = rekursive(a);
	
	printf("ergebnis: %d\n",erg);
	
	return 0;
}

int rekursive(int a)
{
	if(a > 0) {
		a--;
		a = rekursive(a);
	}
	return a;
}
