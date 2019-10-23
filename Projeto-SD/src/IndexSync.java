import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IndexSync {
    String file = "../files/index_";

    public IndexSync(int serverNo) {
        file += serverNo + ".txt";
    }

    public void saveUsers(HashMap<String, HashSet<String>> index){
        try{
            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(index);

            o.close();
            f.close();

            /*FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            HashMap<String, HashSet<String>> hashmap= new HashMap<>();

            try {
                hashmap = (HashMap) ois.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for(User u : hashmap){
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
}