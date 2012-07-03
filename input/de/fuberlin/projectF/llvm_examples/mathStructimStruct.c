#include <stdio.h>


struct Erg {
		int ergAB;
		int ergBA;
	};
struct container {
	int a;
	int b;
	struct Erg erg;
};


int main(void) {
	
	struct container cont;
	cont.a = 5;
	cont.b = 7;

	
	cont.erg.ergAB = cont.a - cont.b;
	cont.erg.ergBA = cont.b - cont.a;
	
	printf("SummeAB: %d\n",cont.erg.ergAB);
	printf("SummeBA: %d\n",cont.erg.ergBA);
	
	return 0;
}

