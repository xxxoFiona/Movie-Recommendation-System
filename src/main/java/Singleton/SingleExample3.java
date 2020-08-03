package Singleton;

public class SingleExample3 {
    private  SingleExample3(){

    }
    private enum Singleton{
        INSTANCE;
        private  SingleExample3 singleExample3;
        //jvm保证只调用一次
        Singleton(){
            singleExample3=new SingleExample3();
        }
        private  SingleExample3 getInstance(){
            return singleExample3;
        }
    }


    private  static SingleExample3 getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

}
