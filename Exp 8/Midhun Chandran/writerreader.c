#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>


sem_t wrt; // this semaphore controls access to "cnt" amonst readers and writers
pthread_mutex_t mutex; // this mutex controls access to the "numreader" among readers
int cnt = 1;
int numreader = 0;
int i;

void *writer(void *wno)
{
    sem_wait(&wrt);
    cnt++;

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
    int readercount, writercount, max;
    pthread_t read[5],write[5];
    pthread_mutex_init(&mutex, NULL);
    sem_init(&wrt,0,1);

        printf("\nEnter the number of Readers: ");
        scanf("%d", &readercount);



        printf("\nEnter the number of Writers: ");
        scanf("%d", &writercount);
    if(readercount<0 || writercount<0)
    { printf("\n\n-------------------------------------------\n");
     printf("Error!!!!\nNo of Readers or Writers is invalid(negative)\n");
      printf("------------------------------------------------\n\n");
    }
    else{

    if(readercount==0 && writercount==0)
    {
       printf("\n\n-------------------------------------------\n");
     printf("no threads created\n");
      printf("------------------------------------------------\n\n");
    }
    else if(readercount==0 &&writercount>0)
    {
        printf("\n\n-------------------------------------------\n");
     printf("No reader thread created.only writer thread created\n");
      printf("------------------------------------------------\n\n");
     }
     else if(writercount==0 && readercount>0)
     {
         printf("\n\n-------------------------------------------\n");
      printf("No writer thread created.only reader thread created\n");
       printf("------------------------------------------------\n\n");
     }
     else
     {
         printf("\n\n-------------------------------------------\n");
      printf("both threads created\n");
       printf("------------------------------------------------\n\n");
     }

    if(readercount>=writercount){
        max = readercount;
    }else{
        max = writercount;
    }
    int a[100];  //Just used for numbering the producer and consumer
    for ( i = 0; i <= max; i++)
    {
        a[i] = i;
    }

    printf("***********Initial value of cnt: %d\n",cnt);

    for( i = 0; i < writercount; i++) {
        pthread_create(&write[i], NULL, (void *)writer, (void *)&a[i]);
    }
    for( i = 0; i < readercount; i++) {
        pthread_create(&read[i], NULL, (void *)reader, (void *)&a[i]);
    }

    for( i = 0; i < writercount; i++) {
        pthread_join(write[i], NULL);
    }
    for( i = 0; i < readercount; i++) {
        pthread_join(read[i], NULL);
    }

    printf("*************Final value of cnt: %d\n",cnt);


    pthread_mutex_destroy(&mutex);
    sem_destroy(&wrt);
    }

    return 0;

}
