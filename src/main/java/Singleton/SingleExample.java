package Singleton;

/**
 * 双重同步锁单例模式
 */
public class SingleExample {
    private  SingleExample(){

    }
    private static SingleExample singleExample=null;
    //双重检测机制 消耗降到最低
    // JVM CPU指令重排
    private  static  SingleExample getInstance(){
        if(singleExample==null){
            synchronized (SingleExample.class){
                if(singleExample==null){
                    singleExample=new SingleExample();
                }
            }

        }
        return  singleExample;
    }
}
