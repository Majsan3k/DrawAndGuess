/**
 * This is a thread that prints its own name on repeat with 1 second sleep between the prints.
 *
 * @author Maja Lund
 */

public class T1 extends Thread {

    private String threadName;
    private boolean alive;
    private boolean active;

    /**
     * Class constructor.
     *
     * @param threadName name of the Thread
     */

    public T1(String threadName){
        this.threadName = threadName;
        alive = true;
        active = true;
    }

    /**
     * Kills the thread.
     */
    public void killThread(){
        System.out.println("\nKilling thread 1\n");
        alive = false;
        active = false;
    }

    /**
     * Prints the thread name on repeat, wait 1 second between the prints.
     */
    @Override
    public void run(){
        while(alive){
            while(active) {
                System.out.println(threadName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}