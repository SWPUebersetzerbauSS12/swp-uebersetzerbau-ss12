#include <stdio.h>

struct container {
	int a;
	int b;
	int summe;
};

int main(void) {
	
	struct container cont;
	cont.a = 5;
	cont.b = 7;
	
	cont.summe = cont.a + cont.b;
	
	printf("Summe: %d\n",cont.summe);
	
	return 0;
}

