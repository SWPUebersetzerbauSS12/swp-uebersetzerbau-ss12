#include <stdio.h>

int addition( int summand1 , int summand2 );
int subtraktion ( int minuend , int subtrahent );
int multiplikation ( int minuend , int subtrahent );
int division ( int minuend , int subtrahent );

void sinnlos();

int main(void) {
	
	int op1 = 10;
	int op2 = 5;
	
	int erg;
	
	erg = addition( op1 , op2 );
	printf("%d + %d = %d\n", op1, op2, erg);
	
	erg = subtraktion( op1 , op2 );
	printf("%d - %d = %d\n", op1, op2, erg);
	
	erg = multiplikation( op1 , op2 );
	printf("%d * %d = %d\n", op1, op2, erg);

	erg = division( op1 , op2 );
	printf("%d / %d = %d\n", op1, op2, erg);
	
	sinnlos();
	
	return 0;
}

int addition( int summand1 , int summand2 ) {
	return summand1 + summand2;
}
int subtraktion ( int minuend , int subtrahent ) {
	return minuend - subtrahent;
}

int multiplikation( int summand1 , int summand2 ) {
	return summand1 * summand2;
}
int division ( int minuend , int subtrahent ) {
	return minuend / subtrahent;
}

void sinnlos() {
}
