import java.io.IOException;
import java.util.Scanner;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipeChannel {
    public static void main(String[] args) throws IOException {

        final PipedOutputStream output = new PipedOutputStream();
        final PipedInputStream input = new PipedInputStream(output);
        int readerNo, writerNo;
        Thread readThread[] = new Thread[10];
        Thread writeThread[] = new Thread[10];
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter number of Readers : ");
        readerNo = scan.nextInt();
        System.out.print("Enter number of Writers : ");
        writerNo = scan.nextInt();
        scan.close();
        System.out.print("\n=====================================================\n\n");
        if (readerNo < 0) {
            System.out.println("Negetive value is not permitted");
            System.exit(0);
        }
        if (writerNo < 0) {
            System.out.println("Negetive value is not permitted");
            System.exit(0);
        }
        for (int i = 1; i <= readerNo; i++) {
            readThread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("[ " + Thread.currentThread().getName() + " ] is executing \n");
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
                        output.write(Thread.currentThread().getName().getBytes());
                        System.out.println("[ " + Thread.currentThread().getName() + " ] is executing \n");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                    } catch (IOException e) {
                    }
                }
            });
            writeThread[i].setName("writerProcess" + i);
        }
        int j = 1, k = 1;
        if (readerNo == 0) {
            while (k <= writerNo) {
                writeThread[k].start();
                k++;
            }
        } else if (writerNo == 0) {
            while (j <= readerNo) {
                readThread[j].start();
                j++;
            }
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