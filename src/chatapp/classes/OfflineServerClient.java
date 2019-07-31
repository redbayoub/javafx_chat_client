package chatapp.classes;

import chatapp.classes.model.Message;
import chatapp.classes.model.User;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OfflineServerClient extends Thread implements ServerInterface {
    // email is ip address
    // username is username
    private User curr_user;

    private static final int APP_PORT=4668;
    private ServerSocket server;
    private ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
    public OfflineServerClient(User curr_user) {
        this.curr_user = curr_user;
    }

    @Override
    public synchronized void start() {
        super.start();
        Runnable id_request=new Runnable() {
            @Override
            public void run() {

            }
        };
        executorService.scheduleAtFixedRate(id_request,0 ,60 , TimeUnit.SECONDS);
        try {
            server=new ServerSocket(APP_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client=server.accept();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean send_message(Message msg) {
        return false;
    }

    @Override
    public List<Message> receive_latest_messages() {
        return null;
    }

    @Override
    public List<User> get_users() {
        return null;
    }

    private class net_users_fetcher extends Thread{
        private List<User> net_users=new ArrayList<>();
        @Override
        public void run() {
            // identify all lan ips
            // if defind in net_users list bypass
            // else add to net_users list
            // send curr id
            // recive recived id
            // add recived id to list
            InetAddress localhost = null;
            try {
                localhost = InetAddress.getLocalHost();
                // this code assumes IPv4 is used
                byte[] ip = localhost.getAddress();
                for (int i = 1; i <= 254; i++) {
                    ip[3] = (byte) i;
                    InetAddress address = InetAddress.getByAddress(ip);
                    if(address.isAnyLocalAddress())
                    if (address.isReachable(1000)) {
                        //System.out.println(address + " machine is turned on and can be pinged");
                        boolean pervDifend=false;
                        for (User u:net_users) {
                            if(u.getEmail().equals(address.toString())){
                                pervDifend=true;
                                break;
                            }
                        }
                        if(pervDifend)continue;
                        else {
                            Socket socket=new Socket(address,APP_PORT );
                            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
                            objectOutputStream.writeObject(curr_user);
                            objectOutputStream.flush();
                            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
                            try {
                                Object recived_obj =objectInputStream.readObject();
                                if(recived_obj instanceof User){
                                    net_users.add((User)recived_obj);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            objectOutputStream.close();
                            objectInputStream.close();
                        }
                    }
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class ClientThread extends Thread{
        private Socket clientSocket;
        private InputStream inputStream;
        private OutputStream outputStream;
        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
