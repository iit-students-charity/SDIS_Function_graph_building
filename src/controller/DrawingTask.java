package controller;

import layout.Graphic;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DrawingTask implements Runnable {
    private final Graphic graphic;


    public DrawingTask(Graphic graphic){
        this.graphic = graphic;
    }

    @Override
    public void run() {
        Lock lock = new ReentrantLock();

        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();

            try {
                graphic.update();
            } finally {
                lock.unlock();
            }
        }
    }
}