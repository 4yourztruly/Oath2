package com.example.oath2.file;

import com.example.oath2.security.JWTService;
import com.example.oath2.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
        EntityModel<FileDTO> links = EntityModel.of(dto);

        Link download = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FileController.class).downloadFile(user,dto.name)).withRel("download");
        Link delete =  WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FileController.class).deleteFile(user,dto.name)).withRel("delete");

        links.add(download);
        links.add(delete);

        try{
            String username = user.getUsername();
            System.out.println(username);
            var file = fileService.addFile(dto.name,dto.content, user, dto.folder);
            return ResponseEntity.ok(links);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-file/{fileName}")
    public ResponseEntity<?> deleteFile(@AuthenticationPrincipal User user, @PathVariable String fileName) {
        try{
            var file = fileService.deleteFile(fileName, user);
            return ResponseEntity.ok().body(file.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download-file/{fileName}")
    public ResponseEntity<?> downloadFile(@AuthenticationPrincipal User user, @PathVariable String fileName) {
        try{
            var file = fileService.downloadFile(fileName, user);
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
   }
