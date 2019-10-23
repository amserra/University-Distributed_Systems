import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        } catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} 

    }
}