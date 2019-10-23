import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class UrlSync {
    String file = "../files/url_";

    public UrlSync(int serverNo) {
        file += serverNo + ".txt";
    }

    public void saveUsers(CopyOnWriteArrayList<URL> urlList){
        try{
            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(urlList);

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} 

    }
}