// Implement the Second Readers-Writers problem (Using Process along with PIPE and Message Queue)

import java.io.IOException;
import java.util.Scanner;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

public class PipeChannel {

    public static void main(String[] args) throws IOException {

        final PipedOutputStream output = new PipedOutputStream();
        final PipedInputStream input = new PipedInputStream(output);
        int readerNo, writerNo;
        Thread readThread[] = new Thread[10];
        Thread writeThread[] = new Thread[10];
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter number of Readers: ");
        readerNo = scan.nextInt();
        System.out.print("Enter number of Writers: ");
        writerNo = scan.nextInt();
        scan.close();
        if (readerNo < 0) {
            System.out.println("Number of readers cannot be negetive");
            System.exit(0);
        }
        if (writerNo < 0) {
            System.out.println("Number of writers cannot be negetive");
            System.exit(0);
        }
        for (int i = 1; i <= readerNo; i++) {
            readThread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
                        LocalDateTime now = LocalDateTime.now();
                        System.out.println("Process " + Thread.currentThread().getName() + " is Executing at " +dtf.format(now));
                        int data = input.read();
                        while (data != -1) {
                            data = input.read();
                        }
                        input.close();

                    } catch (IOException e) {
                    }
                }
            });
            readThread[i].setName("readerProcess" + i);
        }
        for (int i = 1; i <= writerNo; i++) {
            writeThread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
                        LocalDateTime now = LocalDateTime.now();
                        output.write(Thread.currentThread().getName().getBytes());
                        System.out.println("Process " + Thread.currentThread().getName() + " is Executing at " + dtf.format(now));
                        Thread.sleep(1000);
                    } catch (IOException e) {
                    }catch (Exception e) {
                    }
                }
            });
            writeThread[i].setName("writerProcess" + i);
        }
        int j = 1, k = 1;
        if (readerNo == 0) {
            // while (k <= writerNo) {
            //     writeThread[k].start();
            //     k++;
            // }
            System.out.println("Readers cannot be zero");
        } else if (writerNo == 0) {
            // while (j <= readerNo) {
            //     readThread[j].start();
            //     j++;
            // }
            System.out.println("Writers cannot be zero");
        } else {
            while (j <= readerNo && k <= writerNo) {

                writeThread[k].start();
                readThread[j].start();
                j++;
                k++;
            }
            while (j <= readerNo) {
                readThread[j].start();
                j++;
            }
            while (k <= writerNo) {
                writeThread[k].start();
                k++;
            }
        }

    }
}