public class MulticastServerInfo {

    private int serverNo;
    private String TCP_ADDRESS;
    private int TCP_PORT;
    private int carga = 0;

    /**
     * @param serverNo
     * @param TCP_ADDRESS
     * @param TCP_PORT
     * @return
     */
    public MulticastServerInfo(int serverNo, String TCP_ADDRESS, int TCP_PORT) {
        this.serverNo = serverNo;
        this.TCP_ADDRESS = TCP_ADDRESS;
        this.TCP_PORT = TCP_PORT;
    }

    public MulticastServerInfo(int serverNo, String TCP_ADDRESS, int TCP_PORT, int carga) {
        this.serverNo = serverNo;
        this.TCP_ADDRESS = TCP_ADDRESS;
        this.TCP_PORT = TCP_PORT;
        this.carga = carga;
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
    public int getCarga() {
        return carga;
    }

    /**
     * @param carga
     */
    public void setCarga(int carga) {
        this.carga = carga;
    }

    public void incrementCarga() {
        this.carga++;
    }

    /**
     * @param otherServerNo
     * @return int
     */
    public int compareTo(int otherServerNo) {
        return this.serverNo - otherServerNo;
    }

    @Override
    public String toString() {
        return "serverNo: " + serverNo + " Ip: " + TCP_ADDRESS + " Port: " + TCP_PORT + "Load: " + carga;
    }

}