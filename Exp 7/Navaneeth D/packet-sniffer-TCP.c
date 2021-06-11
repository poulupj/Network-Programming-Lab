#include<stdio.h>	//For standard things
#include<stdlib.h>	//malloc
#include<string.h>	//memset
#include<netinet/ip_icmp.h>	//Provides declarations for icmp header
#include<netinet/udp.h>	//Provides declarations for udp header
#include<netinet/tcp.h>	//Provides declarations for tcp header
#include<netinet/ip.h>	//Provides declarations for ip header
#include<sys/socket.h>
#include<arpa/inet.h>

char *filter_word ="CORONA";
void ProcessPacket(unsigned char* , int);
void print_ip_header(unsigned char* , int);
void print_tcp_packet(unsigned char* , int);
void PrintData (unsigned char* , int);

int sock_raw;
FILE *logfile;
int tcp=0,i,j;
struct sockaddr_in source,dest;

int main()
{
  int saddr_size , data_size;
  struct sockaddr saddr;
  struct in_addr in;

  unsigned char *buffer = (unsigned char *)malloc(65536); //Its Big!

  logfile=fopen("log.txt","w");
  if(logfile==NULL) printf("Unable to create file.");
  printf("Starting...\n");

  //Create a raw socket that shall sniff
  sock_raw = socket(AF_INET , SOCK_RAW , IPPROTO_TCP);
  if(sock_raw < 0)
  {
    printf("Socket Error\n");
    return 1;
  }
  while(1)
  {
    saddr_size = sizeof saddr;
    //Receive a packet
    data_size = recvfrom(sock_raw , buffer , 65536 , 0 , &saddr , &saddr_size);
    if(data_size <0 )
    {
      printf("Recvfrom error , failed to get packets\n");
      return 1;
    }
    //Now process the packet
    ProcessPacket(buffer , data_size);
  }
  close(sock_raw);
  printf("Finished");
  return 0;
}

void ProcessPacket(unsigned char* buffer, int size)
{
  //Get the IP Header part of this packet
  struct iphdr *iph = (struct iphdr*)buffer;
  switch (iph->protocol) //Check the Protocol and do accordingly...
  {
    case 6:  //TCP Protocol
    ++tcp;
    print_tcp_packet(buffer , size);
    break;
  }
  printf("TCP packets : %d \r",tcp);
}

void print_ip_header(unsigned char* Buffer, int Size)
{
  unsigned short iphdrlen;

  struct iphdr *iph = (struct iphdr *)Buffer;
  iphdrlen =iph->ihl*4;

  memset(&source, 0, sizeof(source));
  source.sin_addr.s_addr = iph->saddr;

  memset(&dest, 0, sizeof(dest));
  dest.sin_addr.s_addr = iph->daddr;

  fprintf(logfile,"\n");
  fprintf(logfile,"IP Header\n");
  fprintf(logfile,"IP Header Length  : %d DWORDS or %d Bytes\n",(unsigned int)iph->ihl,((unsigned int)(iph->ihl))*4);
  fprintf(logfile,"Source IP        : %s\n",inet_ntoa(source.sin_addr));
  fprintf(logfile,"Destination IP   : %s\n",inet_ntoa(dest.sin_addr));
}

void print_tcp_packet(unsigned char* Buffer, int Size)
{
  unsigned short iphdrlen;

  struct iphdr *iph = (struct iphdr *)Buffer;
  iphdrlen = iph->ihl*4;

  struct tcphdr *tcph=(struct tcphdr*)(Buffer + iphdrlen);

  // int header_size =  sizeof(struct ethhdr) + iphdrlen + tcph->doff*4;
	
	// if (Size - header_size == 0 || Size - header_size > 1000)
	// 	return;
	
	// ////FILTERING
	// unsigned char *raw_data = (unsigned char *)malloc((Size - header_size +1)*sizeof(char));
	// memset(raw_data, '\0', Size - header_size +1);
	// strncat(raw_data,Buffer + header_size,Size - header_size);
	// if (strcmp(raw_data,filter_word) != 0){
	// 	free(raw_data);
	// 	return;
	// }
	// free(raw_data);
  fprintf(logfile,"\n\n***********************TCP Packet*************************\n");

  print_ip_header(Buffer,Size);

  fprintf(logfile,"\n");
  fprintf(logfile,"TCP Header\n");
  fprintf(logfile,"Source Port      : %u\n",ntohs(tcph->source));
  fprintf(logfile,"Destination Port : %u\n",ntohs(tcph->dest));
  fprintf(logfile,"Sequence Number    : %u\n",ntohl(tcph->seq));
  fprintf(logfile,"Acknowledge Number : %u\n",ntohl(tcph->ack_seq));
  fprintf(logfile,"Header Length      : %d DWORDS or %d BYTES\n" ,(unsigned int)tcph->doff,(unsigned int)tcph->doff*4);
  fprintf(logfile,"   |-Urgent Flag          : %d\n",(unsigned int)tcph->urg);
	fprintf(logfile,"   |-Acknowledgement Flag : %d\n",(unsigned int)tcph->ack);
	fprintf(logfile,"   |-Push Flag            : %d\n",(unsigned int)tcph->psh);
	fprintf(logfile,"   |-Reset Flag           : %d\n",(unsigned int)tcph->rst);
	fprintf(logfile,"   |-Synchronise Flag     : %d\n",(unsigned int)tcph->syn);
	fprintf(logfile,"   |-Finish Flag          : %d\n",(unsigned int)tcph->fin);
	fprintf(logfile,"   |-Window         : %d\n",ntohs(tcph->window));
	fprintf(logfile,"   |-Checksum       : %d\n",ntohs(tcph->check));
	fprintf(logfile,"   |-Urgent Pointer : %d\n",tcph->urg_ptr);
	fprintf(logfile,"\n");
	fprintf(logfile,"                        DATA Dump                         ");
	fprintf(logfile,"\n");
		
	fprintf(logfile,"IP Header\n");
	PrintData(Buffer,iphdrlen);
		
	fprintf(logfile,"TCP Header\n");
	PrintData(Buffer+iphdrlen,tcph->doff*4);
		
	fprintf(logfile,"Data Payload\n");	
	PrintData(Buffer + iphdrlen + tcph->doff*4 , (Size - tcph->doff*4-iph->ihl*4) );
}

void PrintData (unsigned char* data , int Size)
{
	
	for(i=0 ; i < Size ; i++)
	{
		if( i!=0 && i%16==0)   //if one line of hex printing is complete...
		{
			fprintf(logfile,"         ");
			for(j=i-16 ; j<i ; j++)
			{
				if(data[j]>=32 && data[j]<=128)
					fprintf(logfile,"%c",(unsigned char)data[j]); //if its a number or alphabet
				
				else fprintf(logfile,"."); //otherwise print a dot
			}
			fprintf(logfile,"\n");
		} 
		
		if(i%16==0) fprintf(logfile,"   ");
			fprintf(logfile," %02X",(unsigned int)data[i]);
				
		if( i==Size-1)  //print the last spaces
		{
			for(j=0;j<15-i%16;j++) fprintf(logfile,"   "); //extra spaces
			
			fprintf(logfile,"         ");
			
			for(j=i-i%16 ; j<=i ; j++)
			{
				if(data[j]>=32 && data[j]<=128) fprintf(logfile,"%c",(unsigned char)data[j]);
				else fprintf(logfile,".");
			}
			fprintf(logfile,"\n");
		}
	}
}