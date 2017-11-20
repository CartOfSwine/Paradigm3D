package replay;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReplaySaver {
	public static SimulationRecord loadReplay(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();
			return (SimulationRecord)obj;
		}catch(Exception e) {
			System.out.println("Failed to open file " + fileName + ".");
			System.out.println(e);
		}
		return null;
	}
	
	public static boolean saveReplay(SimulationRecord r, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(r);
			fos.close();
			return true;
		}catch(Exception e) {
			System.out.println("Failed to save file to " + fileName + ".");
			System.out.println(e);
			return false;
		}
	}
	
}
