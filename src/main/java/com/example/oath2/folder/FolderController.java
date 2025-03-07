package com.example.oath2.folder;

import com.example.oath2.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/folder")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/add-folder")
    public ResponseEntity<?> addFolder(@AuthenticationPrincipal User user, @RequestBody FolderDTO dto) {
        try {
            var folder = folderService.addFolder(dto.name, dto.folder, user);
            return ResponseEntity.ok().body(folder.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    public static class FolderDTO {
        private String name;
        private String folder;
    }
}
