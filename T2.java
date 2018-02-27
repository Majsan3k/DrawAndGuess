/**
 * This is a thread that prints its own name on repeat, wait 1 second between the prints.
 *
 * @author Maja Lund
 */

public class T2 implements Runnable {

    private String threadName;
    private boolean alive;
    private boolean active;

    /**
     * Class constructor.
     *
     * @param threadName name of the thread
     */
    public T2(String threadName){
        this.threadName = threadName;
        alive = true;
        active = true;
    }

    /**
     * Pauses and reassumes thread.
     *
     * @param active tells if the thread should be paused or not.
     */
    public void setActiveStatus(boolean active){
        this.active = active;
    }

    /**
     * Kills the thread.
     */
    public void killThread(){
        System.out.println("\nKilling thread 2\n");
        alive = false;
        active = false;
    }

    /**
     * Prints the thread name on repeat, wait 1 second between the prints.
     */
    @Override
    public void run() {
        while(alive){
            while(active){
                System.out.println(threadName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}