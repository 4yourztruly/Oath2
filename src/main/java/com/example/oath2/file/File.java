package com.example.oath2.file;

import com.example.oath2.folder.Folder;
import com.example.oath2.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@RequiredArgsConstructor
@Data
@Entity(name = "files")
public class File {

    @Id
    private final UUID id;

    @Column(nullable = false)
    private String name;

    private String content;

    @ManyToOne
    private final Folder folder;

    @ManyToOne
    private final User user;

    public File() {this.id = null; this.folder = null; this.user = null;}

    public File(String name, String content, User user, Folder folder) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.content = content;
        this.folder = folder;
        this.user = user;
    }
}
