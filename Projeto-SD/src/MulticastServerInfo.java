public class MulticastServerInfo {

    private int serverNo;
    private String TCP_ADDRESS;
    private int TCP_PORT;
    private int carga = 0;

    public MulticastServerInfo(int serverNo, String TCP_ADDRESS, int TCP_PORT) {
        this.serverNo = serverNo;
        this.TCP_ADDRESS = TCP_ADDRESS;
        this.TCP_PORT = TCP_PORT;
    }

    public int getServerNo() {
        return serverNo;
    }

    public void setServerNo(int serverNo) {
        this.serverNo = serverNo;
    }

    public String getTCP_ADDRESS() {
        return TCP_ADDRESS;
    }

    public void setTCP_ADDRESS(String tCP_ADDRESS) {
        TCP_ADDRESS = tCP_ADDRESS;
    }

    public int getTCP_PORT() {
        return TCP_PORT;
    }

    public void setTCP_PORT(int tCP_PORT) {
        TCP_PORT = tCP_PORT;
    }

    public int getCarga() {
        return carga;
    }

    public void setCarga(int carga) {
        this.carga = carga;
    }

    public void incrementCarga() {
        this.carga++;
    }

    public int compareTo(int otherServerNo) {
        return this.serverNo - otherServerNo;
    }

}