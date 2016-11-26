


import java.io.File;
import java.util.ArrayList;

public class get_FilePath {
	// ��� ��������
		public static ArrayList<String> listFiles(final File folder) {
			ArrayList<String> files = new ArrayList<>();
			for (final File fileEntry : folder.listFiles()) {
				if (!fileEntry.isDirectory()) {
					files.add(fileEntry.getAbsolutePath());
				}
			}
			return files;
		}
}
