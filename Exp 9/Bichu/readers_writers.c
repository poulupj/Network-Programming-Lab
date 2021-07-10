#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/msg.h>



//pgm makes use of multiprocessing and ipc using message queues 
//the queue will have only 1 message at a point of time
//count can only have values > 0 throughout the execution
//resource is read by reader and modified by writer



struct message_buffer 
{
	int count;
	int resource;
} message;



void reader(int i)
{

     	//generate a unique key
	key_t key = ftok("message_queue",65);
	
	//msgget returns an identifier in msgid
    	int msgid = msgget(key, 0666 | IPC_CREAT);

	//reader can read whenever it succesfully fetches a message
	//if a writer is active the queue will be empty
    	msgrcv(msgid, &message, sizeof(message), 0, 0);    	   
    	
    	//for first reader
    	if(message.count == 0)
    		printf("Readers have gained access\n");
    	
    	//reader count is increemented
    	message.count++;
    	
	//message is sent back to queue to allow more readers to enter
    	msgsnd(msgid, &message, sizeof(message), 0);  
    	
    	//reading..
    	printf("Reader %d read resource as : %d\n", i, message.resource);
    	
    	//message is fetched again from the queue to decreement reader count
    	msgrcv(msgid, &message, sizeof(message), 0, 0);    	   
    	message.count--;
    	
    	if(message.count == 0)
    		printf("\n");
    	
    	//sending back message to queue with updated count
    	msgsnd(msgid, &message, sizeof(message), 0);
    	
    	return;
}



void writer(int i)
{

	//generate a unique key
	key_t key = ftok("message_queue",65);
	
	// msgget returns an identifier in msgid
    	int msgid = msgget(key, 0666 | IPC_CREAT);
    	
     	while(1)
    	{
    		// msgrcv to receive message
    		msgrcv(msgid, &message, sizeof(message), 0, 0);
    		
    		//write can enter only when there is no active reader => count = 0
    		if(message.count == 0)
    			break;
    		else
    		{
    			//send message back to queue if writer cannot enter
    			msgsnd(msgid, &message, sizeof(message), 0);
    		}
    	}
    	 
    	//writing..
    	printf("A writer has gained access\n");
    	message.resource++;  
    	printf("Writer %d modified resource to : %d\n\n", i, message.resource);
    	
    	//send back message with updated resource
     	msgsnd(msgid, &message, sizeof(message), 0);
     	
    	return;
	
}



void main()
{
	int r,w,i,j,random;
	pid_t pid;
	
	//generate a unique key
	key_t key = ftok("message_queue",65);
	
	// msgget returns an identifier in msgid
    	int msgid = msgget(key, 0666 | IPC_CREAT);
    	
    	//initialize count and resource
    	message.count = 0;
    	message.resource = 1;
    	
    	//send initial message to queue
    	msgsnd(msgid, &message, sizeof(message), 0);
    	
	//input no of readers and writers
	printf("\nNo of Readers : ");
	scanf("%d",&r);
	printf("No of Writers : ");
	scanf("%d",&w);
	
	//check for invalid input
	if(r < 0 || w < 0)
	{
    		printf("\nInvalid input\n\n");
    		//clear last message in message queue	
    		msgrcv(msgid, &message, sizeof(message), 0, 0);
    		exit(0);
	}
	
	i=j=0;
	printf("\nCreating required processes..\n\n"); 
	
	//loop to create all child processes
	while(i<r || j<w)
	{
  		random = rand();
		
		//if condition satisfied => create reader child process
		if(random%2 == 0 && i<r)
    	    	{
    	    		i++;
    	    		pid = fork();
    	    		if (pid == -1) 
	   		{	
	      			perror("fork failed");
	      			exit(0);
	   		}
    	     		else if(pid == 0)
    	     		{
    	     			reader(i);
    	     			exit(0);
    	     		}
    	     	}
		
		//if condition satisfied => create writer child process
		else if(random%2 == 1 && j<w)
    	    	{
    	    		j++;
    	    		pid = fork();
    	    		if (pid == -1) 
	   		{	
	      			perror("fork failed");
	      			exit(0);
	   		}
    	     		else if(pid == 0)
    	     		{
    	     			writer(j);
    	     			exit(0);
    	     		}
    	     	}
		
	}
	  
	//making parent process wait for children to end
	for(int i=0;i<r+w;i++)
    		wait(NULL);
    	
    	//clear last message in message queue	
    	msgrcv(msgid, &message, sizeof(message), 0, 0);
    		
}
