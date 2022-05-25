package com;
import java.io.InputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ConnectionSsh {
    String host;
    String user = "petrov.aleksandr";
    String private_key = "C:\\Users\\Avic\\Desktop\\Java\\new_Services\\key_rsa.pem";

    public ConnectionSsh(String host) {
        this.host = host;
    }

    public String[] runCommand(String command1){
        String [] result = new String[2];
        try{

            JSch jsch = new JSch();
            JSch.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Connected");
            jsch.addIdentity(private_key);
            System.out.println("identity added ");

            Session session=jsch.getSession(user, host, 22);
            session.connect();

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    result[1] = new String(tmp, 0, i);
                    //System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    result[0] = Integer.toString(channel.getExitStatus());
                   // System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }
    return result;
    }
}

