package com.bjsxt.search.controller;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Runablerun implements Runnable {
    private int count = 0;
    //创建Lock对象
    Lock lock = new ReentrantLock();
    @Override
    public synchronized void run() {
        for (int i=0;i<10;i++){

            count++;
            try {
                Thread.sleep(500);
                System.out.println(Thread.currentThread()+" count = "+count);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    public Runablerun() {
    }


}
