#include <stdio.h>

int main()
{
	int i = 0;
	int a = 10;
	
	while(i < a) {
		printf("while #1: i = %d\n",i);
		i++;
	}
	
	while(i > 0) {
		printf("while #2: i = %d\n",i);
		i--;
	}
	
	while(i <= a) {
		printf("while #3: i = %d\n",i);
		i++;
	}
	
	while(i >= 0) {
		printf("while #4: i = %d\n",i);
		i--;
	}
	
	while(i != a) {
		printf("while #5: i = %d\n",i);
		i++;
	}
	
	while(i != 0) {
		printf("while #6: i = %d\n",i);
		i--;
	}
	
	return 0;
}
