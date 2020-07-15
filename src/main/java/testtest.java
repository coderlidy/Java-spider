
class ThreadA {
    public  static int i=0;
}
class WaitTest {
    public static void main(String[] args) throws InterruptedException {
        final Object obj=new Object();
        Thread a=new Thread(){
            public void run(){
                try {
                    Thread.sleep(1000);
                    System.out.println("上");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        a.start();
        a.join(2000);
        System.out.println("下");
    }
}
