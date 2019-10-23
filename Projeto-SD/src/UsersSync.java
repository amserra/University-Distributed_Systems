import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class UsersSync {
    String file = "../files/users_";

    public UsersSync(int serverNo){
        file += serverNo + ".txt";
    }

    public void saveUsers(ArrayList<User> userList){
        try{
            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(userList);

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} 

    }
}