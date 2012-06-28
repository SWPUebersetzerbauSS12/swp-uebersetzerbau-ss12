def int fib(int n){
   if (n <= 1) return n;
   else {
     int fib;
     fib=fib(n-1) + fib(n-2);
     return fib;
   }
}

def int main(){
   int n;
   int fib;
   fib=fib(n);
   print fib;
   return fib;
}