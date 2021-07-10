/**
 * Author: Kowsik Nandagopan D
 * CSE S6, Roll No 31
 * Program: Implement the Second Readers-Writers problem (Using Process along with PIPE and Message Queue) 
 */

#include<stdio.h>
#include<pthread.h> // for threads
#include<semaphore.h> // semaphore
#include<stdlib.h> // for malloc
#include <unistd.h> // pipe

// Shared memory / Global memory
sem_t rd;
int read_p[2], write_p[2]; // reader pipe and writer pipe

// reader / consumer
void* reader (void *rid) {
    int a;
    read(write_p[0], &a, sizeof(a)); // read from writer pipe
    a--;
    printf("Writer %d: %d\n", *((int *)rid), a); 
    write(read_p[1], &a, sizeof(a)); // pass value to reader pipe
}


// writer / producer
void* writer (void *wid) {
    int a;
    read(read_p[0], &a, sizeof(a)); // read value from reader pipe
    a++;
    printf("Reader %d: %d\n", *((int *)wid), a); 
    write(write_p[1], &a, sizeof(a)); // pass value to writer pipe
}


int main() {
    if (pipe(read_p) < 0 || pipe(write_p) < 0)
        exit(1);

    int a = 100;
    // initialize pipe values. (We don't know which thread starts first)
    write(read_p[1], &a, sizeof(a));
    write(write_p[1], &a, sizeof(a));

    // initialization
    int num_readers = 0, num_writers = 0;
    int flag = 0;
    
    // input number of readers and writers required
    while (1) {
        flag = 0;
        printf ("Enter number of readers: ");
        scanf ("%d", &num_readers);
        printf ("Enter number of writers: ");
        scanf ("%d", &num_writers);

        // Error messages
        if (num_readers < 0){
            printf("Error: Number of readers cannot be negative.\n");
            flag = 1;
        }
        if (num_writers < 0) {
            printf("Error: Number of writers cannot be negative.\n");
            flag = 1;
        }
        if (num_readers == 0 && num_writers > 0) {
            printf("No reader thread created, only writer thread created\n");
            flag = 1;
        } else if (num_writers == 0 && num_readers > 0) {
            printf("No writer thread created, only reader thread created\n");
            flag = 1;
        } else if (num_writers == 0 && num_readers == 0) {
            printf("No writer thread created and no reader thread created\n");
            flag = 1;
        }

        if (flag == 0) break; // exit loop
    }

    // reader and writer id aree stored as array so it can be accessed by thread using pointer
    int *reader_id = (int*)malloc(num_readers*sizeof(int));
    int *writer_id = (int*)malloc(num_writers*sizeof(int));
    // dynamically allocating threads
    pthread_t *readers = (pthread_t*)malloc(num_readers*sizeof(pthread_t));
    pthread_t *writers = (pthread_t*)malloc(num_writers*sizeof(pthread_t));
    
    printf("Reader thread and writer thread are created\n");

    // initializing ID array
    for (int i=0; i<num_readers; i++) reader_id[i] = i+1;
    for (int i=0; i<num_writers; i++) writer_id[i] = i+1;

    // Create reader threads
    for (int i=0; i<num_readers; i++){
        pthread_create(&readers[i], NULL, (void *)reader, (void *)&reader_id[i]);
    }

    // Create writer threads
    for (int i=0; i<num_writers; i++){
        pthread_create(&writers[i], NULL, (void *)writer, (void *)&writer_id[i]);
    }

    // wait for threads to complete execution before quiting
    for (int i=0; i<num_readers; i++){
        pthread_join(readers[i], NULL);
    }

    for (int i=0; i<num_writers; i++){
        pthread_join(writers[i], NULL);
    }


    return 0;
}