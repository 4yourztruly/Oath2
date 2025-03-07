package com.example.oath2.file;

import com.example.oath2.folder.Folder;
import com.example.oath2.folder.FolderService;
import com.example.oath2.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FolderService folderService;


    /**
    * This method adds a file bound to the users selected folder and saves it to the database.
     *
    * @param name the name of the file.
     * @param content the content of the file aka a string.
     * @param folder the name of the folder the user wants to add the file to.
     * @param user the user that wants to add the file, it is taken from the jwt token.
     * @return the newly created file.
     * @throws IllegalArgumentException if a file with the same name already exists.
     * @throws NoSuchElementException if the specified folder does not exist.
    */
    public File addFile(String name, String content, User user, String folder) {
        Optional<File> exists = fileRepository.findFileByNameAndUserId(name, user.getId());
        if(exists.isPresent()) {
            throw new IllegalArgumentException("file already exists, pick another name");
        }

        Optional<Folder> folderExist = folderService.findFolderByNameAndUserId(folder, user.getId());
        if(folderExist.isEmpty()) {
            throw new NoSuchElementException("folder does not exist.");
        }

        File file = new File(name, content, user, folderExist.get());
        fileRepository.save(file);
        folderExist.get().getFiles().add(file);
        folderService.saveFolder(folderExist.get());
        return file;
    }

    /**
     * Deletes the chosen file from the users folder and the database.
     * @param name the name of the file the user wants to delete.
     * @param user the user that calls this method, the user is taken from the jwt token.
     * @throws NoSuchElementException if the file does not exist based of the name given by user.
     * @return the newly deleted file.
     * Note to William, this is where I had return String before instead of File and in other methods I have created other solutions for the problem you will see in bruno after calling this method. (loop of object)
     * I left it this way to show the problem I had, I have shown I can solve this problem in a couple of ways i mainly left it this way so you maybe can find a better solution.
     */


    public File deleteFile(String name, User user) {
        Optional<File> file = fileRepository.findFileByNameAndUserId(name, user.getId());
        if(file.isEmpty()) {
            throw new NoSuchElementException();
        }
        String deletedName = file.get().getName();
        file.get().getFolder().getSubFolders().remove(file);
        fileRepository.delete(file.get());
       return file.get();
    }

    /**
     * Downloads the chosen file/ returns the chosen object to the user.
     * @param name the name of the file the user wants to download
     * @param user the user that wants to download the file, taken from the jwt token.
     * @throws NoSuchElementException if the file doesnt exist
     * @return the chosen object the user wants to download
     * Note to William this is where i solve this problem i had in the method above by being more specific in the controller.
     */

    public File downloadFile(String name, User user) {
        Optional<File> file = fileRepository.findFileByNameAndUserId(name, user.getId());
        if(file.isEmpty()) {
            throw new NoSuchElementException("File does not exist");
        }
        return file.get();
    }
}
