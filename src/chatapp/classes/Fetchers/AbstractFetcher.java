package chatapp.classes.Fetchers;

import chatapp.classes.Messages;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractFetcher {
    private final static ScheduledExecutorService service= Executors.newScheduledThreadPool(5);
    private volatile boolean running=true;
    private ScheduledFuture<?> task;

    private final Runnable process=new Runnable() {
        @Override
        public void run() {
            if(running)  run_fetcher();
        }
    };


    protected final void scheduleAtFixedRate(long period, TimeUnit tu){
        task=service.scheduleAtFixedRate(process, 0, period, tu);
    }



    protected abstract void run_fetcher();

    public final void stopThread(){
        Messages.mediaPlayers.clear();
        running=false;
        task.cancel(true);
    }
}
