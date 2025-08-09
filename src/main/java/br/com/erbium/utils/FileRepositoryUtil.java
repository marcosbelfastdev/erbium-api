package br.com.erbium.utils;

import lombok.NonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

/**
 * Utility class for safe and flexible file I/O operations.
 * <p>
 * Features:
 * <ul>
 *     <li>Read/write/append/delete files</li>
 *     <li>Thread-safe write operations</li>
 *     <li>Cross-platform path validation</li>
 *     <li>Automatic directory creation (optional)</li>
 *     <li>File listing with optional recursive and extension filtering</li>
 * </ul>
 */
public class FileRepositoryUtil {

    private static final ConcurrentHashMap<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();

    /**
     * Reads the content of a file as a UTF-8 encoded string.
     *
     * @param file File to read
     * @return File content as string
     * @throws IOException If the file doesn't exist or cannot be read
     */
    public static String readFile(File file) throws IOException {
        validateFile(file);
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    /**
     * Validates and returns a File object from the given path.
     * Ensures the file exists and is not a directory.
     *
     * @param path Full path to the file
     * @return File object representing the existing file
     * @throws IOException If the file doesn't exist or is not a valid file
     */
    public static File readFile(@NonNull String path) throws IOException {
        File file = new File(path);
        validateFile(file);
        return file;
    }

    /**
     * Writes content to the specified file path.
     * Will not create directories if they do not exist.
     *
     * @param filePath Path to the file
     * @param content  Content to write
     * @throws IOException If writing fails
     */
    public static void writeFile(String filePath, String content) throws IOException {
        writeFile(new File(filePath), content, false);
    }

    /**
     * Writes content to the specified file.
     * Will not create directories if they do not exist.
     *
     * @param file    File to write
     * @param content Content to write
     * @throws IOException If writing fails
     */
    public static void writeFile(File file, String content) throws IOException {
        writeFile(file, content, false);
    }

    /**
     * Writes content to the specified file path, with optional directory creation.
     *
     * @param filePath            Path to the file
     * @param content             Content to write
     * @param createDirsIfMissing Whether to create missing parent directories
     * @throws IOException If writing fails
     */
    public static void writeFile(String filePath, String content, boolean createDirsIfMissing) throws IOException {
        writeFile(new File(filePath), content, createDirsIfMissing);
    }

    /**
     * Writes content to the specified file, with optional directory creation.
     * Thread-safe: ensures only one thread writes to the same file at a time.
     *
     * @param file                File to write
     * @param content             Content to write
     * @param createDirsIfMissing Whether to create missing parent directories
     * @throws IOException If writing fails
     */
    public static void writeFile(@NonNull File file, String content, boolean createDirsIfMissing) throws IOException {
        validatePath(file.getPath(), createDirsIfMissing);
        executeWithFileLock(file, () -> Files.writeString(file.toPath(), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
    }

    /**
     * Duplicates a file in the same directory using a fixed name.
     * Overwrites the target file if it already exists.
     *
     * @param sourceFile The file to duplicate.
     * @return The duplicated file.
     * @throws IOException If the source doesn't exist or the copy fails.
     */
    public static File saveLastKnownGoodFile(@NonNull File sourceFile, @NonNull String destinationFilePath) throws IOException {
        validateFile(sourceFile);

        File destinationFile = new File(destinationFilePath);

        // Delete the destination file if it already exists (thread-safe)
        if (destinationFile.exists()) {
            executeWithFileLock(destinationFile, () -> {
                Files.delete(destinationFile.toPath());
                return null;
            });
        }

        // Copy the file using lock on the source
        return executeWithFileLock(sourceFile, () -> {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            return destinationFile;
        });
    }

    /**
     * Appends content to an existing file.
     * Will not create directories if they do not exist.
     *
     * @param file    File to append to
     * @param content Content to append
     * @throws IOException If append fails
     */
    public static void appendToFile(File file, String content) throws IOException {
        appendToFile(file, content, false);
    }

    /**
     * Appends content to an existing file, with optional directory creation.
     * Thread-safe: ensures only one thread writes to the same file at a time.
     *
     * @param file                File to append to
     * @param content             Content to append
     * @param createDirsIfMissing Whether to create missing parent directories
     * @throws IOException If append fails
     */
    public static void appendToFile(File file, String content, boolean createDirsIfMissing) throws IOException {
        validatePath(file.getPath(), createDirsIfMissing);
        executeWithFileLock(file, () -> Files.writeString(file.toPath(), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }

    /**
     * Lists all regular files in a directory, non-recursively.
     *
     * @param directory Directory to list
     * @param recursive Whether to include subdirectories
     * @return List of regular files
     * @throws IOException If the directory is invalid
     */
    public static List<File> listFilesInDirectory(File directory, boolean recursive) throws IOException {
        return listFilesInDirectory(directory, recursive, null);
    }

    /**
     * Lists regular files in a directory, with optional recursion and extension filter.
     *
     * @param directory       Directory to list
     * @param recursive       Whether to include subdirectories
     * @param extensionFilter Optional file extension (e.g., "json", "txt")
     * @return List of files matching filter
     * @throws IOException If the directory is invalid
     */
    public static List<File> listFilesInDirectory(File directory, boolean recursive, String extensionFilter) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException("Directory does not exist: " + directory.getAbsolutePath());
        }

        List<File> files = new ArrayList<>();

        Files.walk(directory.toPath(), recursive ? Integer.MAX_VALUE : 1)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    if (extensionFilter == null || path.toString().toLowerCase().endsWith("." + extensionFilter.toLowerCase())) {
                        files.add(path.toFile());
                    }
                });

        return files;
    }

    /**
     * Deletes a file from disk.
     *
     * @param file File to delete
     * @return true if deleted successfully, false otherwise
     * @throws IOException If the file doesn't exist or deletion fails
     */
    public static boolean deleteFile(File file) throws IOException {
        validateFile(file);
        return executeWithFileLock(file, () -> Files.deleteIfExists(file.toPath()));
    }

    /**
     * Validates and optionally creates the parent directories of the path.
     *
     * @param path                Path to validate
     * @param createDirsIfMissing Whether to create missing parent directories
     * @throws IOException If path is invalid or inaccessible
     */
    public static void validatePath(String path, boolean createDirsIfMissing) throws IOException {
        Path normalizedPath = Paths.get(path).toAbsolutePath().normalize();
        Path parent = normalizedPath.getParent();

        if (parent != null && !Files.exists(parent)) {
            if (createDirsIfMissing) {
                Files.createDirectories(parent);
            } else {
                throw new FileNotFoundException("Directory does not exist: " + parent);
            }
        }

        if (!isPathValid(normalizedPath.toString())) {
            throw new IOException("Invalid path format for OS: " + normalizedPath);
        }
    }

    /**
     * Validates that a file exists and is not a directory.
     *
     * @param file File to check
     * @throws IOException If not a valid file
     */
    private static void validateFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IOException("Not a file: " + file);
        }
    }

    /**
     * Validates whether the given path string is valid for the current operating system.
     *
     * @param path Path string
     * @return true if valid, false otherwise
     */
    public static boolean isPathValid(String path) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            //return !path.matches(".*[<>:\"/\\\\|?*].*");
            return true;
        }
        return path != null && !path.trim().isEmpty();
    }

    /**
     * Executes a given action while holding a lock on the specified file.
     * This ensures thread-safe operations on the file.
     *
     * @param file   The file to lock.
     * @param action The action to execute.
     * @param <T>    The return type of the action.
     * @return The result of the action.
     * @throws IOException If an I/O error occurs during the action.
     */
    private static <T> T executeWithFileLock(File file, IOCallable<T> action) throws IOException {
        ReentrantLock lock = fileLocks.computeIfAbsent(file.getAbsolutePath(), k -> new ReentrantLock());
        int timeoutSeconds = 30; // Define a reasonable timeout

        try {
            if (lock.tryLock(timeoutSeconds, TimeUnit.SECONDS)) {
                try {
                    return action.call();
                } finally {
                    Thread.sleep(2000);
                    lock.unlock();
                }
            } else {
                throw new IOException("Failed to acquire lock for file: " + file.getAbsolutePath() + " within " + timeoutSeconds + " seconds.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new IOException("File lock acquisition interrupted for file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Functional interface for operations that can throw IOException.
     * Used with {@link #executeWithFileLock(File, IOCallable)}.
     *
     * @param <T> The return type of the operation.
     */
    @FunctionalInterface
    private interface IOCallable<T> {
        T call() throws IOException;
    }

    /**
     * Saves the current state of this Workspace object to a file on disk.
     * The object and all its contained children must be serializable.
     *
     * @param path The full path (including filename) where the object will be saved.
     * @return This Workspace instance for fluent chaining.
     * @throws RuntimeException if an I/O error occurs during the save process.
     */
    public static void save(Object object, @NonNull String path) {

        Thread thread = new Thread(() -> {
        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(object);

        } catch (IOException e) {
            System.err.println("Failed to save object to path: " + path);
            e.printStackTrace();
        }
    });

    thread.setDaemon(true); // optional: don’t block JVM exit
    thread.start();

    }
}