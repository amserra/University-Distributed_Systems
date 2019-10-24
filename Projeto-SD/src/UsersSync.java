import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class UsersSync {
    String file = "files/users_";

    public UsersSync(int serverNo) {
        file += serverNo + ".txt";
    }

    public UsersSync(){
        file += ".txt";
    }

    public void saveUsers(CopyOnWriteArrayList<User> userList) {
        try {

            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(userList);

            o.close();
            f.close();

            /*FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            ArrayList<User> arraylist = new ArrayList<>();

            try {
                arraylist = (ArrayList) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            for(User u : arraylist){
                System.out.println(u.getUsername());
            }

            ois.close();
            fis.close();*/
        } catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} 
    }

    public void syncUsers(CopyOnWriteArrayList<User> userList){
        
    }
}