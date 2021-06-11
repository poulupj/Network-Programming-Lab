#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include<stdlib.h>


sem_t wrt;
pthread_mutex_t mutex;
int counter = 0;
int numreader = 0;

void *writer(void *wno)
{   
    sem_wait(&wrt);
    counter++;
    printf("W%d [MODIFIED]\n",(*((int *)wno)));
    sem_post(&wrt);

}
void *reader(void *rno)
{   
    
    pthread_mutex_lock(&mutex);
    numreader++;
    counter--;
    if(numreader == 1) {
        sem_wait(&wrt); 
    }
    
    pthread_mutex_unlock(&mutex);
    
    printf("R%d [READ]\n",*((int *)rno));

    
    pthread_mutex_lock(&mutex);
    numreader--;
    if(numreader == 0) {
        sem_post(&wrt); 
    }
    pthread_mutex_unlock(&mutex);
}

int main()
{   
    int r,w;
    printf("Number of READERS : ");
    scanf("%d",&r);
    printf("Number of WRITERS : ");
    scanf("%d",&w);
    pthread_t read[50],write[50];
    pthread_mutex_init(&mutex, NULL);
    sem_init(&wrt,0,1);
    if(r<0 || w<0)
    {
     printf("Error : \nInvalid inputs\n");
    }
    else{
    int a[50];
    for(int i=0;i<r || i<w;i++)
    {
     a[i]=i+1;
    }
    if(r==0 && w==0)
    {
        printf("----------------------------------------------------------------\nNO READERS OR WRITERS\n----------------------------------------------------------------\n");
        exit(0);
    }
    else 
    {
        if(r == 0){
            printf("\n----------------------------------------------------------------\n\nNO READERS\n");
        }
        else{
            printf("----------------------------------------------------------------\n\nREADERS\n\n");
            for(int i = 0 ; i < r ; i++)
            {
            printf("R%d\t",i);
            }
            printf("\n");
        }
        printf("\n----------------------------------------------------------------\n");
        if(w == 0 )
        {
            printf("\nNO WRITERS\n\n----------------------------------------------------------------\n\n");
        }
        else{

        printf("\nWRITERS\n\n");
        for(int i = 0 ; i < w ; i++)
        {
            printf("W%d\t",i);
        }
        printf("\n\n----------------------------------------------------------------\n\n");
        }
    }
     
     printf("=======================\tOperation log\t=======================\n\n");
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
    if(counter == w-r)
    {
     printf("\nVALUE AFTER OPERATIONS :  %d\n",counter);
    }
    pthread_mutex_destroy(&mutex);
    sem_destroy(&wrt);
    }
    return 0;
    
}

