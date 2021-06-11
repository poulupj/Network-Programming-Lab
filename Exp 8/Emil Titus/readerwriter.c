#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

sem_t canwrite;                                 // semaphore to control write permission
pthread_mutex_t mutex;                          // mutex to modify reader count
int A = 100;                                    // variable to simulate shared memory
int readercount = 0;                            // number of readers currently reading

void * writer(void * arg){                      // Writer thread
    int operand = 1;    
    if((int)arg < 0)                                    // separate ID from operand
        operand = -1; 
    int id = ((int) arg) * operand;
    srand(time(NULL) - (10 * id));
    usleep(rand() % 10000 + 1);
    printf("    Writer #%d is waiting to write.\n", id);
    sem_wait(&canwrite);                                // wait for permission to write (binary semaphore)
    A += operand;                                       // write to (modify) the shared memory
    printf("    Writer #%d modified the value in \'A\'. Now, A = %d.\n", id, A);
    sem_post(&canwrite);                                // signal the completion of write operation to other blocked threads
}

void * reader(void * arg){                       // Reader thread
    int id = (int) arg;
    srand(time(NULL) - (10 * id));
    usleep(rand() % 10000 + 1);
    printf("    Reader #%d is waiting to read.\n", id);
    pthread_mutex_lock(&mutex);                         // lock mutex before updating current reader count
    readercount++;
    if(readercount == 1)                                // first reader should block writers               
        sem_wait(&canwrite);
    pthread_mutex_unlock(&mutex);                       // unlock mutex
    usleep(rand() % 5000 + 1);
    printf("    Reader #%d is reading the value of \'A\'. Now, A = %d.\n", id, A);
    pthread_mutex_lock(&mutex);                         
    readercount--;
    if(readercount == 0){                               // last reader should allow writers to write
        printf("    No readers are currently reading.\n");
        sem_post(&canwrite);
    }
    pthread_mutex_unlock(&mutex);
}

int main(int argc, char const *argv[]){   
    if(argc != 3){                              // check whether arguments are specified
        printf("   USAGE : %s <number_of_readers> <number_of_writers>\n", argv[0]);
        printf(" EXAMPLE : %s 5 2\n", argv[0]);
        return 1;
    }

    int r = atoi(argv[1]);
    int w = atoi(argv[2]);
    if(r < 0 || w < 0){
        printf(" Negative values are not permitted.\n");
        return 1;
    }

    int multiplier = 1;
    int i;
    pthread_t readers[r], writers[w];           // declaring the reader and writer threads
    pthread_mutex_init(&mutex, NULL);           // initializing the mutex
    sem_init(&canwrite, 0, 1);                  // initializing the semaphore with a value 1

    printf(" Creating %d readers and %d writers.\n Initially, A = 100.\n\n", r, w);

    for(i = 0; i < r; i++)                      // create the reader threads
        pthread_create(&readers[i], NULL, (void *) reader, (void *) (i + 1));
    
    // The ID is multiplied with the operand used to modify the shared memory, to combine two arguments into one.

    for(i = 0; i < w; i++, multiplier *= -1)    // create the writer threads
        pthread_create(&writers[i], NULL, (void *) writer, (void *) ((i + 1) * multiplier));

    for(i = 0; i < r; i++)                      // wait for readers to complete execution
        pthread_join(readers[i], NULL);

    for(i = 0; i < w; i++)
        pthread_join(writers[i], NULL);         // wait for writers to complete execution

    printf("\n There are no readers or writers left.\n Exiting...\n");

    pthread_mutex_destroy(&mutex);              // destroy the mutex
    sem_destroy(&canwrite);                     // destroy the semaphore

    return 0;
}