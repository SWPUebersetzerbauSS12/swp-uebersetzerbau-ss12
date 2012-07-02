#include <stdio.h>

struct container {
	int a;
	int b;
	int erg;
};

int main(void) {
	
	struct container cont;
	int bla = 0; 
	struct container diff;
	cont.a = 5;
	cont.b = 7;

	diff.b=3;
	diff.a=10;
	
	cont.erg = cont.a + cont.b;
	diff.erg = diff.b - diff.a;
	
	printf("Summe: %d\n",cont.erg);
	printf("Summe: %d\n",diff.erg);
	
	return 0;
}

