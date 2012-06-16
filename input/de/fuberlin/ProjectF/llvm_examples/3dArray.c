#include <stdio.h>

int main()
{
	int i,j;
	int array[2][2][2];
	
	array[0][0][0] = 0;
	array[0][1][0] = 1;
	array[1][0][0] = 1;
	array[1][1][0] = 1;
	
	array[0][0][1] = 0;
	array[0][1][1] = 0;
	array[1][0][1] = 0;
	array[1][1][1] = 1;
	
	printf("OR:\n");
	for(i = 0; i < 2; i++)
		for(j = 0; j < 2; j++)
			printf("%d or %d is %d\n", i, j, array[i][j][0]);
			
	printf("AND:\n");
	for(i = 0; i < 2; i++)
		for(j = 0; j < 2; j++)
			printf("%d and %d is %d\n", i, j, array[i][j][1]);
	
	return 0;
}
