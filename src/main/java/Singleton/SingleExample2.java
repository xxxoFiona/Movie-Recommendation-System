package Singleton;

/**
 * 双重同步锁单例模式
 */
public class SingleExample2 {
    private  SingleExample2(){

    }
    private volatile static SingleExample2 singleExample=null;
    //双重检测机制 消耗降到最低
    // JVM CPU指令重排 ---解决方案 volatile +双重检测   实现线程安全
    private  static  SingleExample2 getInstance(){
        if(singleExample==null){
            synchronized (SingleExample2.class){
                if(singleExample==null){
                    singleExample=new SingleExample2();
                }
            }

        }
        return  singleExample;
    }
}
