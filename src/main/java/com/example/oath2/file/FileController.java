package com.example.oath2.file;

import com.example.oath2.security.JWTService;
import com.example.oath2.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JWTService jwtService;

    @PostMapping("/add-file")
    public ResponseEntity<?> addFile(@AuthenticationPrincipal User user, @RequestBody FileDTO dto) {
        try{
            String username = user.getUsername();
            System.out.println(username);
            var file = fileService.addFile(dto.name,dto.content, user, dto.folder);
            return ResponseEntity.ok().body(file.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-file")
    public ResponseEntity<?> deleteFile(@AuthenticationPrincipal User user, @RequestBody DeleteFileDTO dto) {
        try{
            var file = fileService.deleteFile(dto.name, user);
            return ResponseEntity.ok().body(file.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download-file")
    public ResponseEntity<?> downloadFile(@AuthenticationPrincipal User user, @RequestBody DownloadFileDTO dto) {
        try{
            var file = fileService.downloadFile(dto.name, user);
            return ResponseEntity.ok().body("name: " + file.getName() + "\rcontent: " + file.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    public static class FileDTO {
        private String name;
        private String content;
        private String folder;
    };

    @Data
    public static class DeleteFileDTO {
        private String name;
    };

    @Data
    public static class DownloadFileDTO {
        private String name;
    };

}
