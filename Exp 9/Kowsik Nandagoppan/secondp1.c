/**
 * Author: Kowsik Nandagopan D
 * CSE S6, Roll No 31
 * Program: Implement the First Readers-Writers Problem (Using Threads and Shared Memory) 
 */


#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

int main() {
    int fd;
    char* fifo = "./fifo";
    mkfifo(fifo, 0666); // file name and permission
    char arr1[80], arr2[80];

    while (1) {
        fd = open(fifo, O_WRONLY); // FIFO in write only mode
        fgets(arr2, 80, stdin);

        write(fd, arr2, strlen(arr2)+1);
        close(fd);

        fd = open(fifo, O_RDONLY); // FIFO in write only mode
        fgets(arr1, 80, stdin);

        write(fd, arr1, strlen(arr1)+1);
        printf("Writer 2: %s\n", arr1);
        close(fd);
    }
    return 0;
}