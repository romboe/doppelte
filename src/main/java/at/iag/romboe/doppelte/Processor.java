package at.iag.romboe.doppelte;



import static java.nio.file.Files.isDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

public class Processor {

	private static final int DEPTH = 10;
	private static final String DIRECTORY_OF_DUPLICATES = "/doppelte";
	private String sourceDir;
	private String backupDir;

	public Processor(String sourceDir, String backupDir) {
		this.sourceDir = sourceDir;
		this.backupDir = backupDir;
	}

	public void go() throws IOException {
		createDirectoyOfDuplicates();

		Set<Path> filePaths = getFilePathsFromDirectory(sourceDir);
		printFileNames(filePaths);

		for (Path filePath:filePaths) {
			if (fileExistsInBackupDirectory(filePath)) {
				// move file
				File file = filePath.toFile();
				Path targetPath = Paths.get(file.getParent(), DIRECTORY_OF_DUPLICATES, file.getName());
				file.renameTo(targetPath.toFile());
			}
		}
	}

	public Set<Path> getFilePathsFromDirectory(String dir) throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
			return stream
				.filter(file -> !isDirectory(file)).collect(Collectors.toSet());
		}
	}

	private void printFileNames(Set<Path> files) {
		files.stream().map(Path::getFileName).map(Path::toString).forEach(s -> System.out.println(s));
	}

	private boolean fileExistsInBackupDirectory(Path fileToLookFor) throws IOException {
		try (Stream<Path> stream = Files.walk(Paths.get(backupDir), DEPTH)) {
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
