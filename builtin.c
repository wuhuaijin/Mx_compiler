 #include <stdio.h>
 #include <stdlib.h>
 #include <string.h>

 int string_length(char* ptr){
     return strlen(ptr);
 }

 char* string_substring(char* ptr, int left, int right){
     char* ret = malloc(right - left + 1);
     ret[right - left] = '\0';
     memcpy(ret, ptr + left, right - left);
     return ret;
 }

 int string_parseInt(char* ptr){
 	int x;
 	sscanf(ptr, "%d", &x);
 	return x;
 }

 int string_ord(char* ptr, int idx){
     return ptr[idx];
 }

 char* string_add(char* ptr1, char* ptr2){
 	int len1 = strlen(ptr1);
 	int len2 = strlen(ptr2);
 	char* ret = malloc(len1+len2+1);
 	strcpy(ret, ptr1);
 	strcat(ret, ptr2);
 	return ret;
 }

 int string_eq(char* ptr1, char* ptr2){
 	return strcmp(ptr1, ptr2) == 0;
 }
 int string_neq(char* ptr1, char* ptr2){
 	return strcmp(ptr1, ptr2) != 0;
 }
 int string_lt(char* ptr1, char* ptr2){
 	return strcmp(ptr1, ptr2) < 0;
 }
 int string_le(char* ptr1, char* ptr2){
 	return strcmp(ptr1, ptr2) <= 0;
 }
 int string_gt(char* ptr1, char* ptr2){
 	return strcmp(ptr1, ptr2) > 0;
 }
 int string_ge(char* ptr1, char* ptr2){
 	return strcmp(ptr1, ptr2) >= 0;
 }

 void print(char* s){
 	printf("%s", s);
 }

 void println(char* s){
 	printf("%s\n", s);
 }

 void printInt(int x){
 	printf("%d", x);
 }

 void printlnInt(int x){
 	printf("%d\n", x);
 }

 char* getString(){
 	char* ret = malloc(1200);
 	scanf("%s", ret);
 	return ret;
 }

 int getInt(){
 	int x;
    scanf("%d", &x);
    return x;
 }

 char* toString(int x){
 	char* ret = malloc(1200);
     sprintf(ret, "%d", x);
     return ret;
 }	







