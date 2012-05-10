
int addition( int summand1 , int summand2 );
int subtraktion ( int minuend , int subtrahent );

void sinnlos();

int main(void) {
	
	int op1 = 10;
	int op2 = 5;
	
	int summe;
	int differenz;
	
	summe = addition( op1 , op2 );
	differenz = subtraktion( op1 , op2 );
	
	sinnlos();
	
	return 0;
}

int addition( int summand1 , int summand2 ) {
	return summand1 + summand2;
}
int subtraktion ( int minuend , int subtrahent ) {
	return minuend - subtrahent;
}

void sinnlos() {
}
