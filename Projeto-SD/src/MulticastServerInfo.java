/**
 * Class that represents a MulticastServer, with all its properties
 */
public class MulticastServerInfo {

    private int serverNo;
    private String TCP_ADDRESS;
    private int TCP_PORT;
    private int load = 0;

    /**
     * Used by the RMIServer when a new MulticastServer connects and by the
     * MulticastServer when it recieves an anwser from the RMIServer
     * 
     * @param serverNo    Assigned by the RMIServer
     * @param TCP_ADDRESS Args[0]
     * @param TCP_PORT    Args[1]
     */
    public MulticastServerInfo(int serverNo, String TCP_ADDRESS, int TCP_PORT) {
        this.serverNo = serverNo;
        this.TCP_ADDRESS = TCP_ADDRESS;
        this.TCP_PORT = TCP_PORT;
        this.load = 0;
    }

    /**
     * Used when the RMIBackupServer needs to add a new MulticastServer
     * 
     * @param serverNo    Assigned by the RMIServer
     * @param TCP_ADDRESS Args[0]
     * @param TCP_PORT    Args[1]
     * @param load        Ammount of work(indexing links) assigned to this
     *                    MulticastServer
     */
    public MulticastServerInfo(int serverNo, String TCP_ADDRESS, int TCP_PORT, int load) {
        this.serverNo = serverNo;
        this.TCP_ADDRESS = TCP_ADDRESS;
        this.TCP_PORT = TCP_PORT;
        this.load = load;
    }

    /**
     * @return int
     */
    public int getServerNo() {
        return serverNo;
    }

    /**
     * @param serverNo
     */
    public void setServerNo(int serverNo) {
        this.serverNo = serverNo;
    }

    /**
     * @return String
     */
    public String getTCP_ADDRESS() {
        return TCP_ADDRESS;
    }

    /**
     * @param tCP_ADDRESS
     */
    public void setTCP_ADDRESS(String tCP_ADDRESS) {
        TCP_ADDRESS = tCP_ADDRESS;
    }

    /**
     * @return int
     */
    public int getTCP_PORT() {
        return TCP_PORT;
    }

    /**
     * @param tCP_PORT
     */
    public void setTCP_PORT(int tCP_PORT) {
        TCP_PORT = tCP_PORT;
    }

    /**
     * @return int
     */
    public int getLoad() {
        return load;
    }

    /**
     * @param load
     */
    public void setLoad(int load) {
        this.load = load;
    }

    /**
     * Increments by 1 unit the load parameter
     */
    public void incrementLoad() {
        this.load++;
    }

    /**
     * @param otherServerNo Given serverNo
     * @return int Compares the given serverNo with this
     */
    public int compareTo(int otherServerNo) {
        return this.serverNo - otherServerNo;
    }

    @Override
    public String toString() {
        return "serverNo: " + serverNo + " Ip: " + TCP_ADDRESS + " Port: " + TCP_PORT + "Load: " + load;
    }

}