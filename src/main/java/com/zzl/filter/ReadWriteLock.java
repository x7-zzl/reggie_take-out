package com.zzl.filter;
/*编写一个程序，实现JDK中ReadWriteLock的功能。

        ReadWriteLock读写锁：

        读锁中的代码执行时，写锁中的代码被阻塞；写锁中的代码执行时，读锁中的代码也会被阻塞，也就是读锁和写锁会相互阻塞；

        但是读锁中的代码执行时，其他加读锁的代码不会受影响。*/
public class ReadWriteLock {
    private int readLocks=0;
    private int writeLocks=0;
    private int wait=0;

    public synchronized void lock() throws  InterruptedException{
        while ((writeLocks>0)){
            wait();
        }
        readLocks++;
    }

    public synchronized void release(){
        readLocks--;
        notifyAll();
    }

    public synchronized void accquire() throws InterruptedException {
        wait++;
        while (wait>0||readLocks>0){
            wait();
        }
        wait--;
        writeLocks++;
    }

    public synchronized void release2(){
        readLocks--;
        notifyAll();
    }


    public static void main(String[] args) {

    }
}
