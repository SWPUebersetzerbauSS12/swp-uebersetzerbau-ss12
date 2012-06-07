#include <stdio.h>

int main()
{
	int i = 0;
	int a = 5;
	int summe = 0;
	
	while(i < 10) {
		summe = summe + a;
		i++;
	}
	
	printf("%d * 10 = %d\n", a, summe);
	return 0;
}
