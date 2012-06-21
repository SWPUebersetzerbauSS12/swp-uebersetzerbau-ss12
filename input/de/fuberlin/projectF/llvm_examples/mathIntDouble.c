#include <stdio.h>


int addition( int summand1 , int summand2 );
double additionDouble( double summand1 , double summand2 );

int subtraktion ( int minuend , int subtrahent );

void sinnlos();

int main(void) {
	
	int op1 = 10;
	int op2 = 5;
	double op1Double = 3.4;
	double op2Double = 2.9;
	
	int summe;
	double summeDouble;
	
	int differenz;
	
	summe = addition( op1 , op2 );
	printf("%d + %d = %d\n", op1, op2, summe);
	
	summeDouble = op1Double + op2Double;
	printf("%f + %f = %f\n", op1Double, op2Double, summeDouble);
	
	summeDouble = additionDouble( op1Double , op2Double );
	printf("%f + %f = %f\n", op1Double, op2Double, summeDouble);
	
	differenz = subtraktion( op1 , op2 );
	printf("%d + %d = %d\n", op1, op2, differenz);
	
	sinnlos();
	
	return 0;
}

int addition( int summand1 , int summand2 ) {
	return summand1 + summand2;
}
int subtraktion ( int minuend , int subtrahent ) {
	return minuend - subtrahent;
}

double additionDouble( double summand1 , double summand2 ) {
	return summand1 + summand2;
}

void sinnlos() {
}
