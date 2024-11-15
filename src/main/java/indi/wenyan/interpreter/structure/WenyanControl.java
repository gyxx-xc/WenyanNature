package indi.wenyan.interpreter.structure;

import java.util.concurrent.Semaphore;

public class WenyanControl {
    public Semaphore entitySemaphore;
    public Semaphore programSemaphore;

    public WenyanControl(Semaphore entitySemaphore, Semaphore programSemaphore) {
        this.entitySemaphore = entitySemaphore;
        this.programSemaphore = programSemaphore;
    }

    public void wait_tick(){
        entitySemaphore.release(1);
        try {
            programSemaphore.acquire(1);
        } catch (InterruptedException e) {
            throw new WenyanException("killed");
        }
    }
}
