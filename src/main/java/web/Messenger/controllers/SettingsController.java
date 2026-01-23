package web.Messenger.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import web.Messenger.Data.Res;
import web.Messenger.models.Chat;
import web.Messenger.models.User;
import web.Messenger.repo.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class SettingsController {

    private String generateSafeFileName(Long userId, MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);

        // Используем комбинацию ID пользователя, временной метки и случайной строки
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomString = UUID.randomUUID().toString().substring(0, 8);

        // Формат: user_{id}_{timestamp}_{random}.{ext}
        return String.format("user_%d_%s_%s%s",
                userId, timestamp, randomString,
                fileExtension != null ? fileExtension : ".jpg");
    }
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    private void deleteOldAvatar(User user) throws IOException {
        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            Path oldAvatarPath = Paths.get(user.getAvatarPath());
            if (Files.exists(oldAvatarPath)) {
                Files.delete(oldAvatarPath);
            }
        }
    }
    @Autowired
    private web.Messenger.Services.SessionService sessionService;
    @Autowired
    private UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @GetMapping("/settings/{id}")
    public String settingsPage(@PathVariable(value = "id") Long uid, Model model, HttpSession session,
                               HttpServletRequest request, HttpServletResponse response) {
        Long userId = sessionService.getUserIdFromSessionOrCookie(request, response, session);
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<User> user = userRepository.findById(uid);
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        if (!userId.equals(uid)){
            return "redirect:/settings/" + userId;
        }
        model.addAttribute("user", user.get());

        return "settings";
    }
    @PostMapping("/settings/{id}/upload")
    public String uploadImage(@PathVariable(value = "id") Long uid, @RequestParam("file") MultipartFile file,
                              Model model, HttpSession session, HttpServletRequest request,
                              HttpServletResponse response) throws IOException {

        Long userId = sessionService.getUserIdFromSessionOrCookie(request, response, session);
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Генерируем безопасное имя файла
        String safeFileName = generateSafeFileName(userId, file);
        Path filePath = uploadPath.resolve(safeFileName);

        // Сохраняем файл
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Удаляем старый файл аватара, если он существует
        deleteOldAvatar(user);

        // Сохраняем относительный путь в базе данных
        String relativePath = uploadDir + safeFileName;
        user.setAvatarPath(relativePath);
        userRepository.save(user);
        model.addAttribute("user", user);

        return "settings";
    }
}
