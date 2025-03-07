package com.example.oath2.folder;

import com.example.oath2.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * Adds a folder to the users specified folder.
     * @param name the name of the folder
     * @param folderName the name of the folder where the new folder is to be placed.
     * @param user the user that wants to add the folder, taken from jwt token.
     * @throws IllegalArgumentException if the name of the new folder already exists.
     * @throws NoSuchElementException if the name of the folder where the new folder should be placed does not exist.
     * @return the newly created folder.
     */

    public Folder addFolder(String name, String folderName, User user) {
        Optional<Folder> isUnique = folderRepository.findFolderByNameAndUserId(name, user.getId());

        if(isUnique.isPresent()) {
            throw new IllegalArgumentException("name of folder already exists please pick choose another name.");
        }

        Optional<Folder> inputFolder = folderRepository.findFolderByNameAndUserId(folderName, user.getId());
        if(inputFolder.isEmpty()) {
            throw new NoSuchElementException("folder does not exist");
        }

        Folder folderObject = folderRepository.findFolderByNameAndUserId(folderName,user.getId()).get();
        Folder folder = new Folder(name, user, folderObject, null);
        folderRepository.save(folder);
        folderObject.getSubFolders().add(folder);
        folderRepository.save(folderObject);
        return folder;
    }

    /**
     * This method is a method that already exists in the repository but is placed here so other services can use the method.
     * @param name name of the folder the user wants to find.
     * @param id the uuid of the user.
     * @return returns an optional that either has the folder or not.
     */

    public Optional<Folder> findFolderByNameAndUserId(String name, UUID id) {
        return folderRepository.findFolderByNameAndUserId(name, id);
    }

    /**
     * this method is a method of one that already exists in the repository but is here to other services can use it.
     * this method saves a folder to the database.
     * @param folder insert a folder object that the user wants to save.
     */

    public void saveFolder(Folder folder) {
        folderRepository.save(folder);
    }
}
