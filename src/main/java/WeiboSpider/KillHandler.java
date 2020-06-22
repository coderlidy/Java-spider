package WeiboSpider;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class KillHandler implements SignalHandler {

    public void registerSignal(String signalName) {
        Signal signal = new Signal(signalName);
        Signal.handle(signal, this);
    }

    public void handle(Signal signal) {

        if (signal.getName().equals("TERM")) {
            System.out.println(signal.getName());
        } else if (signal.getName().equals("INT") || signal.getName().equals("HUP")) {
            System.out.println(signal.getName());
        } else {
            System.out.println(signal.getName());
        }
    }

}