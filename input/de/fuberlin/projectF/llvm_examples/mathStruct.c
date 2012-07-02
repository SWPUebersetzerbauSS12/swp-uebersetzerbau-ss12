#include <stdio.h>

struct container_int {
	int a;
	int b;
	int erg;
};

struct container_double {
	double a;
	double b;
	double erg;
};

int main(void) {
	
	struct container_int cont_int;
	struct container_double cont_double;
	
	cont_int.a = 5;
	cont_int.b = 7;
	
	cont_double.a = 10.6;
	cont_double.b = 1.3;
	
	cont_int.erg = cont_int.a + cont_int.b;
	cont_double.erg = cont_double.a + cont_double.b;
	
	printf("Summe: %d\n",cont_int.erg);
	printf("Summe: %f\n",cont_double.erg);
	
	return 0;
}

