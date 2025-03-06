package code;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-06 11:14
 **/
public class LockTest {

    public void l1(){
        ReentrantReadWriteLock r1 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.WriteLock writeLock = r1.writeLock();
        writeLock.lock();
    }

}
