#include <stdio.h>

int main()
{
	int ia = 5;
	int ib = 6;
	
	double fa = 5.3;
	double fb = 3.2;
	
	int ierg;
	double ferg;
	
	
	ierg = ia + ib;
	printf("(int)%d + (int)%d = (int)%d\n", ia, ib, ierg);
	
	ferg = ia + ib;
	printf("(int)%d + (int)%d = (double)%f\n", ia, ib, ferg);
	
	ierg = ia + fb;
	printf("(int)%d + (double)%f = (int)%d\n", ia, fb, ierg);
	
	ferg = ia + fb;
	printf("(int)%d + (double)%f = (double)%f\n", ia, fb, ferg);
	
	ierg = fa + ib;
	printf("(double)%f + (int)%d = (int)%d\n", fa, ib, ierg);
	
	ferg = fa + ib;
	printf("(double)%f + (int)%d = (double)%f\n", fa, ib, ferg);
	
	ierg = fa + fb;
	printf("(double)%f + (double)%f = (int)%d\n", fa, fb, ierg);
	
	ferg = fa + fb;
	printf("(double)%f + (double)%f = (double)%f\n", fa, fb, ferg);
	
	return 0;
}
