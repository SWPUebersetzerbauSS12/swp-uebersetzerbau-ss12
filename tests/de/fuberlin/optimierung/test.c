#include <stdio.h>
#include <stdlib.h>

struct test {
  int a;
  float b;
};

int main(int argc, char **argv) {
  struct test a;
  a.a = 5;
  a.b = 5.0;
  printf("int:%i",a.a);
  return a.a;
} 
