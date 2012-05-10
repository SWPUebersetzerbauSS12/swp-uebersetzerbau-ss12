
int addition( int summand1 , int summand2 );
int additionArray( int* opArray);
double additionDouble( double summand1 , double summand2 );

int subtraktion ( int minuend , int subtrahent );

void sinnlos();

int main(void) {
	
	int op1 = 10;
	int op2 = 5;
	int opArray[2];
	
	double op1Double = 3.4;
	double op2Double = 2.9;
	
	int summe;
	double summeDouble;
	
	int differenz;
	
	summe = addition( op1 , op2 );
	opArray[0] = op1;
	opArray[1] = op2;
	summe = additionArray( opArray );
	
	summeDouble = addition( op1Double , op2Double );
	
	differenz = subtraktion( op1 , op2 );
	
	sinnlos();
	
	return 0;
}

int addition( int summand1 , int summand2 ) {
	return summand1 + summand2;
}

int additionArray( int* opArray ) {
	return opArray[0] + opArray[1];
}

int subtraktion ( int minuend , int subtrahent ) {
	return minuend - subtrahent;
}

double additionDouble( double summand1 , double summand2 ) {
	return summand1 + summand2;
}

void sinnlos() {
}
