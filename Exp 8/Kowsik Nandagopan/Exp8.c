/**
 * Author: Kowsik Nandagopan D
 * CSE S6, Roll No 31
 * Program: Implement the First Readers-Writers Problem (Using Threads and Shared Memory) 
 */

#include<stdio.h>
#include<pthread.h> // for threads
#include<semaphore.h> // semaphore
#include<stdlib.h> // for malloc

// Shared memory / Global memory
pthread_mutex_t mutex;
sem_t wrt;
int count_readers = 0;
int a = 100;

// reader / consumer
void* reader (void *rid) {

    // lock mutex before changing number of readers in waiting
    pthread_mutex_lock(&mutex);
        count_readers++;
        if (count_readers == 1) sem_wait(&wrt); // if first reader, then stop write to access
    pthread_mutex_unlock(&mutex);

    a--; // decrement a
    printf("Reader %d: %d\n", *((int *)rid), a); // print a

     // lock mutex before changing number of readers in waiting
    pthread_mutex_lock(&mutex);
        count_readers--;
        if (count_readers == 0) sem_post(&wrt); // if last reader, writer can access
    pthread_mutex_unlock(&mutex);
}


// writer / producer
void* writer (void *wid) {
    sem_wait(&wrt); // semaphore wait before other writers can access
        a++; // increment a
        printf("Writer %d: %d\n", *((int *)wid), a); // print a
    sem_post(&wrt); // release semaphore lock
}

int main() {

    // initialization
    int num_readers = 0, num_writers = 0;
    sem_init(&wrt, 0, 1); // semaphore - for thread - binary format (0, 1)
    pthread_mutex_init(&mutex, NULL); // init mutex
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
            printf("Error: Number of readers cannot be negative.\n");
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

    // Destroy mutex and semaphores
    pthread_mutex_destroy(&mutex);
    sem_destroy(&wrt);

    return 0;
}
