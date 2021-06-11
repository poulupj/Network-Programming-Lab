#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>


sem_t wrt;
pthread_mutex_t mutex;
int shared_resource = 1;
int numreader = 0;

void *writer(void *wno)
{
    sem_wait(&wrt);
    shared_resource++;
    printf("\n******************\n");
    printf("Writer %d produced item. shared_resource: %d\n\n",(*((int *)wno)),shared_resource);
    sem_post(&wrt);

}
void *reader(void *rno)
{
    pthread_mutex_lock(&mutex);
    numreader++;
    if(numreader == 1) {
        sem_wait(&wrt);
    }
    pthread_mutex_unlock(&mutex);

    printf("\n******************\n");
    printf("Reader %d: consumed item. shared_resource: %d\n",*((int *)rno),shared_resource);
    shared_resource--;
    printf("Current value of shared_resource: %d\n\n",shared_resource);


    pthread_mutex_lock(&mutex);
    numreader--;
    if(numreader == 0) {
        sem_post(&wrt);
    }
    pthread_mutex_unlock(&mutex);
}

int main()
{
    int numReaders, numWriters, max;
    pthread_t read[100],write[100];
    pthread_mutex_init(&mutex, NULL);
    sem_init(&wrt,0,1);

    while (1)
    {
        printf("\nNumber of Readers: ");
        scanf("%d", &numReaders);
        if(numReaders<=0){
            printf("Number should be greater than 0");
        }else{
            break;
        }
    }

    while (1)
    {
        printf("\nNumber of Writers: ");
        scanf("%d", &numWriters);
        if(numWriters<=0){
            printf("Number oshould be greater than 0");
        }else{
            break;
        }
    }

    if(numReaders>=numWriters){
        max = numReaders;
    }else{
        max = numWriters;
    }
    int index[100];
    for (int i = 0; i <= max; i++)
    {
        index[i] = i;
    }
    printf("\n\n******************\n");
    printf("Initial value of shared_resource: %d\n",shared_resource);
    printf("******************\n\n");
    for(int i = 0; i < numWriters; i++) {
        pthread_create(&write[i], NULL, (void *)writer, (void *)&index[i]);
    }
    for(int i = 0; i < numReaders; i++) {
        pthread_create(&read[i], NULL, (void *)reader, (void *)&index[i]);
    }

    for(int i = 0; i < numWriters; i++) {
        pthread_join(write[i], NULL);
    }
    for(int i = 0; i < numReaders; i++) {
        pthread_join(read[i], NULL);
    }
    printf("\n\n******************\n");
    printf("shared_resource: %d\n",shared_resource);
    printf("******************\n");

    pthread_mutex_destroy(&mutex);
    sem_destroy(&wrt);

    return 0;

}
