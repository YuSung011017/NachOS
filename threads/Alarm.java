package nachos.threads;

import nachos.machine.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
	KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    private Map<KThread,Long>waitingQueue = new HashMap<>(); //  대기 중인 스레드를 추가하고
    // 타이머 인터럽트 핸들러에서는 해당 시간이 경과한 스레드를 찾아서 깨우는 데 활용할 수 있는 waitingQueue를 Map형태로 구현
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	long wakeTime = Machine.timer().getTime() + x;
	//while (wakeTime > Machine.timer().getTime())
	//    KThread.yield();
    if(x<=0)
        return;
    Machine.interrupt().disable(); //인터럽트 비활성화
    waitingQueue.put(KThread.currentThread(),wakeTime); //waitingQueue에 현재 thread와 waketime을 저장
    KThread.sleep();//현재 thread를 block
    Machine.interrupt().enable();//watingTime이 지난 후 다시 interrupt를 받을 수 있게 함.
    }
    public static void alarmTest1() {
        int durations[] = {1000, 10*1000, 100*1000};
        long t0, t1;

        for (int d : durations) {
            t0 = Machine.timer().getTime();
            ThreadedKernel.alarm.waitUntil (d);
            t1 = Machine.timer().getTime();
            System.out.println ("alarmTest1: waited for " + (t1 - t0) + " ticks");
        }
    }

    // Implement more test methods here ...

    // Invoke Alarm.selfTest() from ThreadedKernel.selfTest()
    public static void selfTest() {
        alarmTest1();

        // Invoke your other test methods here ...
    }
}
