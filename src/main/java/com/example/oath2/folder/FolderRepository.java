package com.example.oath2.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    public Optional<Folder>findFolderByNameAndUserId(String name, UUID userId);
}
