#include <stdio.h>

double addition( double summand1 , double summand2 );
double subtraktion ( double minuend , double subtrahent );
double multiplikation ( double minuend , double subtrahent );
double division ( double minuend , double subtrahent );

void sinnlos();

int main(void) {
	
	double op1 = 1.9;
	double op2 = 5.2;
	
	double erg;
	
	erg = addition( op1 , op2 );
	printf("%f + %f = %f\n", op1, op2, erg);
	
	erg = subtraktion( op1 , op2 );
	printf("%f - %f = %f\n", op1, op2, erg);
	
	erg = multiplikation( op1 , op2 );
	printf("%f * %f = %f\n", op1, op2, erg);

	erg = division( op1 , op2 );
	printf("%f / %f = %f\n", op1, op2, erg);
	
	sinnlos();
	
	return 0;
}

double addition( double summand1 , double summand2 ) {
	return summand1 + summand2;
}
double subtraktion ( double minuend , double subtrahent ) {
	return minuend - subtrahent;
}

double multiplikation( double summand1 , double summand2 ) {
	return summand1 * summand2;
}
double division ( double minuend , double subtrahent ) {
	return minuend / subtrahent;
}

void sinnlos() {
}
