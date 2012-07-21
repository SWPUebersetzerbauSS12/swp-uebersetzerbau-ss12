#include <stdio.h>

struct Ergebnisse {
	int IntSum;
	int IntDif;
	int IntMul;
	int IntDiv;
	
	double DoubleSum;
	double DoubleDif;
	double DoubleMul;
	double DoubleDiv;
};

int main()
{
	int i;
	int int1 = 31;
	int int2 = 28;
	
	double double2 = 3.9;
	double double1 = 2.4;
	
	struct Ergebnisse erg;
	
	int array[11];
	
	//Integer vergleiche
	printf("%d == %d\t", int1, int2);
	if(int1 == int2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%d != %d\t", int1, int2);
	if(int1 != int2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%d < %d\t", int1, int2);
	if(int1 < int2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%d > %d\t", int1, int2);
	if(int1 > int2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%d <= %d\t", int1, int2);
	if(int1 <= int2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%d >= %d\t", int1, int2);
	if(int1 >= int2)
		printf("true\n");
	else
		printf("wrong\n");
		
	//Double vergleiche
	printf("%f == %f\t", double1, double2);
	if(double1 == double2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%f != %f\t", double1, double2);
	if(double1 != double2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%f < %f\t", double1, double2);
	if(double1 < double2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%f > %f\t", double1, double2);
	if(double1 > double2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%f <= %f\t", double1, double2);
	if(double1 <= double2)
		printf("true\n");
	else
		printf("wrong\n");
	
	printf("%f >= %f\t", double1, double2);
	if(double1 >= double2)
		printf("true\n");
	else
		printf("wrong\n");
		
	//Berechnungen + record
	erg.IntSum = int1 + int2;
	printf("%d + %d = %d\n", int1, int2, erg.IntSum);
	erg.IntDif = int1 - int2;
	printf("%d - %d = %d\n", int1, int2, erg.IntDif);
	erg.IntMul = int1 * int2;
	printf("%d * %d = %d\n", int1, int2, erg.IntMul);
	erg.IntDiv = int1 / int2;
	printf("%d / %d = %d\n", int1, int2, erg.IntDiv);
	
	erg.DoubleSum = double1 + double2;
	printf("%f + %f = %f\n", double1, double2, erg.DoubleSum);
	erg.DoubleDif = double1 - double2;
	printf("%f - %f = %f\n", double1, double2, erg.DoubleDif);
	erg.DoubleMul = double1 * double2;
	printf("%f * %f = %f\n", double1, double2, erg.DoubleMul);
	erg.DoubleDiv = double1 / double2;
	printf("%f / %f = %f\n", double1, double2, erg.DoubleDiv);
	
	//Arrays
	for(i = 0; i < 10; i++) {
		array[i] = 0;
	}
	
	printf("Bevor: ");
	for(i = 0; i < 10; i++) {
		printf("%d ", array[i]);
	}
	printf("\n");
	
	printf("After: ");
	for(i = 0; i < 10; i++) {
		array[i] = i;
	}
	
	for(i = 0; i < 10; i++) {
		printf("%d ", array[i]);
	}
	printf("\n");
	
	return 0;
}
	
	
	
