#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>


sem_t wrt;
pthread_mutex_t mutex;
int cnt = 0;
int numreader = 0;

void *writer(void *wno)
{   
    sem_wait(&wrt);
    cnt++;
    printf("Writer %d modified\n",(*((int *)wno)));
    sem_post(&wrt);

}
void *reader(void *rno)
{   
    // Reader wants to enter the critical section.wait(mutex)
    pthread_mutex_lock(&mutex);
    numreader++;//number of reader increases
    cnt--;
    if(numreader == 1) {
        sem_wait(&wrt); // If this  the first reader, then it will block the writer.atleast one reader in critical section
    }
    //other readers can enter during this reader is in critical section.(signal)
    pthread_mutex_unlock(&mutex);
    // Reading Section
    printf("Reader %d: read\n",*((int *)rno));

    // current reader performs reading(wait)
    pthread_mutex_lock(&mutex);
    numreader--;
    if(numreader == 0) {
        sem_post(&wrt); // If this is the last reader, it will wake up the writer.
    }
    pthread_mutex_unlock(&mutex);//reader leaves(signal)
}

int main()
{   
    int r,w;
    printf("Enter no of readers\n");
    scanf("%d",&r);
    printf("Enter No of writers\n");
    scanf("%d",&w);
    printf("expected final count value is no of writers - no of readers=%d\n",w-r);
    pthread_t read[50],write[50];
    pthread_mutex_init(&mutex, NULL);
    sem_init(&wrt,0,1);
    if(r<0 || w<0)
    {
     printf("Error!!!!\nNo of Readers or Writers is invalid\n");
    }
    else{
    int a[50];
    for(int i=0;i<r || i<w;i++)
    {
     a[i]=i+1;
    }
    if(r==0 && w==0)
    {
     printf("no threads created\n");
    }
    else if(r==0 && w>0)
    {
     printf("No reader thread created.only writer thread created\n");
     }
     else if(w==0 && r>0)
     {
      printf("No writer thread created.only reader thread created\n");
     }
     else
     {
      printf("both threads created\n");
     }
     
    for(int i = 0; i < r; i++) {
        pthread_create(&read[i], NULL, (void *)reader, (void *)&a[i]);
    }
    for(int i = 0; i < w; i++) {
        pthread_create(&write[i], NULL, (void *)writer, (void *)&a[i]);
    }

    for(int i = 0; i < r; i++) {
        pthread_join(read[i], NULL);
    }
    for(int i = 0; i < w; i++) {
        pthread_join(write[i], NULL);
    }
    if(cnt == w-r)
    {
     printf("final count value is  %d.Hence success\n",cnt);
    }
    pthread_mutex_destroy(&mutex);
    sem_destroy(&wrt);
    }
    return 0;
    
}

