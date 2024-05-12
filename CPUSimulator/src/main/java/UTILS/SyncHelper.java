package UTILS;

public class SyncHelper {

    public static final Object monitor;
    private static boolean isRead;
    static {
        monitor= new Object();
        isRead= false;
    }

    public static void waitForNotification() {
        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyThread() {
        synchronized (monitor) {
            monitor.notify();
        }
    }
}
