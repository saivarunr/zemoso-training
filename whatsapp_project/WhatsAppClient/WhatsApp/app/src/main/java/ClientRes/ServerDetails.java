package ClientRes;

/**
 * Created by zemoso on 3/8/16.
 */
public class ServerDetails {
    private final static String serverAddress="http://10.10.11.16:9000";
    //Home IP 192.168.1.2

    private final static String homeAddr="http://192.168.1.4:9000";
    public static String getServerAddress(){
        return serverAddress;
    }
}
