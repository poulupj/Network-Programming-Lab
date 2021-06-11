/*
First readers writers problem using mutex and semaphore.
Navaneeth D
Roll no 43
*/

#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>


sem_t wrt; // this semaphore controls access to "cnt" amonst readers and writers
pthread_mutex_t mutex; // this mutex controls access to the "numreader" among readers 
int cnt = 1;
int numreader = 0;

void *writer(void *wno)
{   
    sem_wait(&wrt);
    cnt++;
    printf("-------Writer-------\n");
    printf("Writer %d incremented cnt to %d\n\n",(*((int *)wno)),cnt);
    sem_post(&wrt);

}
void *reader(void *rno)
{   
    // Reader acquire the lock before modifying numreader
    pthread_mutex_lock(&mutex);
    numreader++;
    if(numreader == 1) {
        sem_wait(&wrt); // If this id the first reader, then it will block the writer
    }
    pthread_mutex_unlock(&mutex);
    // Reading Section
    printf("-------Reader-------\n");
    printf("Reader %d: read cnt as %d\n",*((int *)rno),cnt);
    cnt--;
    printf("Reader %d: decremented cnt to %d\n\n",*((int *)rno),cnt);

    // Reader acquire the lock before modifying numreader
    pthread_mutex_lock(&mutex);
    numreader--;
    if(numreader == 0) {
        sem_post(&wrt); // If this is the last reader, it will wake up the writer.
    }
    pthread_mutex_unlock(&mutex);
}

int main()
{   
    int nr, nw, max;
    pthread_t read[5],write[5];
    pthread_mutex_init(&mutex, NULL);
    sem_init(&wrt,0,1);
    
    while (1)
    {
        printf("\nEnter the number of Readers: ");
        scanf("%d", &nr);
        if(nr<=0){
            printf("Number of readers cannot be negetive or zero");
        }else{
            break;
        }
    }
    
    while (1)
    {
        printf("\nEnter the number of Writers: ");
        scanf("%d", &nw);
        if(nw<=0){
            printf("Number of writers cannot be negetive or zero");
        }else{
            break;
        }
    }
    
    if(nr>=nw){
        max = nr;
    }else{
        max = nw;
    }
    int a[100];  //Just used for numbering the producer and consumer
    for (int i = 0; i <= max; i++)
    {
        a[i] = i;
    }
    printf("\n\n------------------------------\n");
    printf("Initial value of cnt: %d\n",cnt);
    printf("------------------------------\n\n");
    for(int i = 0; i < nw; i++) {
        pthread_create(&write[i], NULL, (void *)writer, (void *)&a[i]);
    }
    for(int i = 0; i < nr; i++) {
        pthread_create(&read[i], NULL, (void *)reader, (void *)&a[i]);
    }
    
    for(int i = 0; i < nw; i++) {
        pthread_join(write[i], NULL);
    }
    for(int i = 0; i < nr; i++) {
        pthread_join(read[i], NULL);
    }
    printf("\n\n------------------------------\n");
    printf("Final value of cnt: %d\n",cnt);
    printf("------------------------------\n");

    pthread_mutex_destroy(&mutex);
    sem_destroy(&wrt);

    return 0;
    
}