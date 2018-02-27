/**
 * Multithreading program that starts one thread, waits 5 seconds and then starts another thread. The two threads run together
 * in 5 seconds and then the second thread is paused for 5 seconds. 5 seconds after the restart of the second thread
 * the first thread stops. The second thread continues in 5 seconds and then stops.
 *
 * @author Maja Lund
 */

public class ThreadTester {

    /**
     * Starts one thread. Waits 5 seconds and then starts another thread. After another 5 seconds the second thread pauses
     * for 5 seconds. Waits 5 more seconds after the second thread has been restarted and then the first thread stops.
     * After another 5 seconds the second thread stops.
     */
    public void run(){
        System.out.println("\nCreating and starting thread 1\n");
        T1 t1 = new T1("Thread 1");
        t1.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nCreating and starting thread 2\n");
        T2 t2 = new T2("Thread 2");
        Thread thread2 = new Thread(t2);
        thread2.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nPausing thread 2\n");
        t2.setActiveStatus(false);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nRestarting thread 2\n");
        t2.setActiveStatus(true);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.killThread();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t2.killThread();
    }

    public static void main(String[] args){
        ThreadTester threadTester = new ThreadTester();
        threadTester.run();
    }
}
