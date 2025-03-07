package com.example.oath2.folder;

import com.example.oath2.file.File;
import com.example.oath2.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "folders")
public class Folder {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "folder")
    private final List<File>files = new ArrayList<>();

    @ManyToMany
    private final List<Folder> subFolders = new ArrayList<>();

    @ManyToOne
    private final Folder parentFolder;

    @ManyToOne
    private final User user;

    public Folder() {this.id = null; this.user =null; this.parentFolder = null;}

    public Folder(String name, User user, Folder parentFolder, Folder subFolder) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.user = user;
        this.parentFolder = parentFolder;
        this.subFolders.add(subFolder);
    }

}
