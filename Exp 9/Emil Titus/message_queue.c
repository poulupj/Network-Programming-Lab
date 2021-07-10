#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/msg.h>

struct message_format{
    int value;
    int readercount;
} message;

void reader(int i){
    key_t key = ftok("MQueue", 24);
    int msgid = msgget(key, 0666 | IPC_CREAT);
    msgrcv(msgid, &message, sizeof(message), 0, 0);           
    message.readercount++;
    printf("Reader #%d: Shared Resource Value = %d\nNumber of readers currently reading: %d\n\n", i, message.value, message.readercount);
    msgsnd(msgid, &message, sizeof(message), 0);  
    msgrcv(msgid, &message, sizeof(message), 0, 0);           
    message.readercount--;    
    if(message.readercount == 0)
        printf("Currently no readers are reading.\n\n");
    else
        printf("Number of readers currently reading: %d\n\n", message.readercount);
    msgsnd(msgid, &message, sizeof(message), 0);
    return;
}

void writer(int i){
    key_t key = ftok("MQueue", 24);
    int msgid = msgget(key, 0666 | IPC_CREAT);
    while(1){
        msgrcv(msgid, &message, sizeof(message), 0, 0);
        if(message.readercount == 0)
            break;
        else
            msgsnd(msgid, &message, sizeof(message), 0);
    }
    message.value++;  
    printf("Writer #%d modified the resource. Shared Resource Value = %d\n\n", i, message.value);
    msgsnd(msgid, &message, sizeof(message), 0);
    return;
}



int main(int argc, char const *argv[]){   
    if(argc != 3){
        printf("  USAGE : %s <number_of_readers> <number_of_writers>\n", argv[0]);
        printf("EXAMPLE : %s 5 2\n", argv[0]);
        return 1;
    }
    int r = atoi(argv[1]);
    int w = atoi(argv[2]);
    if(r < 0 || w < 0){
        printf(" Negative values are not permitted.\n");
        return 1;
    }
    int i = 0, j = 0, random;
    pid_t pid;
    key_t key = ftok("MQueue", 24);
    int msgid = msgget(key, 0666 | IPC_CREAT);
    message.readercount = 0;
    message.value = 100;
    msgsnd(msgid, &message, sizeof(message), 0);    
    printf("Creating %d readers and %d writers.\nInitially, Shared Resource Value = 100.\n\n", r, w);
    while(i < r || j < w){
        random = rand();
        if(random % 2 == 0 && i < r){
            i++;
            pid = fork();
            if (pid == -1){    
                perror("Could not fork!");
                exit(0);
            }
            else if(pid == 0){
                reader(i);
                exit(0);
            }
        }
        else if(random % 2 == 1 && j < w){
            j++;
            pid = fork();
            if (pid == -1){    
                perror("Could not fork!");
                  exit(0);
               }
            else if(pid == 0){
                 writer(j);
                 exit(0);
            }
        }
    }
    for(int i=0;i<r+w;i++)
        wait(NULL);
    msgrcv(msgid, &message, sizeof(message), 0, 0);
}