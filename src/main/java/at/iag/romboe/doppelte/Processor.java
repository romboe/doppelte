package at.iag.romboe.doppelte;



import static java.nio.file.Files.isDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

public class Processor {

	private static final int DEPTH = 20;
	private static final String DIRECTORY_OF_DUPLICATES = "/doppelte";
	private String sourceDir;
	private String backupDir;
	private Path pathOfLastFind;

	public Processor(String sourceDir, String backupDir) {
		this.sourceDir = sourceDir;
		this.backupDir = backupDir;
	}

	public void go() throws IOException {
		createDirectoyOfDuplicates();

		List<Path> filePaths = getFilePathsFromDirectory(sourceDir);
		printFileNames(filePaths);

		for (Path filePath:filePaths) {
			if (fileExistsInDirectory(filePath, pathOfLastFind) || fileExistsInBackupDirectory(filePath)) {
				// move file
				File file = filePath.toFile();
				Path targetPath = Paths.get(file.getParent(), DIRECTORY_OF_DUPLICATES, file.getName());
				file.renameTo(targetPath.toFile());
			}
		}
	}

	public List<Path> getFilePathsFromDirectory(String dir) throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
			return stream
				.filter(file -> !isDirectory(file))
				.sorted()
				.collect(Collectors.toList());
		}
	}

	private void printFileNames(List<Path> files) {
		files.stream().map(Path::getFileName).map(Path::toString).forEach(s -> System.out.println(s));
	}

	private boolean fileExistsInBackupDirectory(Path fileToLookFor) throws IOException {
		return fileExistsInDirectory(fileToLookFor, Paths.get(backupDir));
	}

	private boolean fileExistsInDirectory(Path fileToLookFor, Path directory) throws IOException {
		if (null == directory) {
			return false;
		}
		try (Stream<Path> stream = Files.walk(directory, DEPTH)) {
			Optional<Path> result = stream
				.filter(file -> !Files.isDirectory(file))
				.filter(file -> {
					try {
						boolean ident = FileUtils.contentEquals(file.toFile(), fileToLookFor.toFile());
						if (ident) {
							System.out.println(fileToLookFor + " hier gefunden: " + file);
						}
						return ident;
					}
					catch(IOException e) {
						System.err.println(e.getMessage());
						return false;
					}
				})
				.findFirst();
			if (result.isPresent()) {
				this.pathOfLastFind = result.get().getParent();
			}
			return result.isPresent();
		}
	}

	private void createDirectoyOfDuplicates() {
		File duplicateDir = new File(sourceDir + DIRECTORY_OF_DUPLICATES);
		if (!duplicateDir.exists()){
			duplicateDir.mkdirs();
		}
	}
}
