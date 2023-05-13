package ch.eglisi.mdm_project_two.controller;

import ch.eglisi.mdm_project_two.model.ClassificationDto;
import ch.eglisi.mdm_project_two.service.ImageClassificationModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
public class EndpointController {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EndpointController.class);
    private final ImageClassificationModel classificationModel = new ImageClassificationModel();

    @GetMapping("/")
    public String index() {
        logger.info("Index called");
        return "index";
    }

    @PostMapping("/upload")
    public String upload(Model model, @RequestParam("image") MultipartFile imageFile) {
        logger.info("Upload called");
        if (imageFile.isEmpty()) {
            return "index";
        }
        var classifications = classificationModel.predict(imageFile);
        var classificationsDto = ClassificationDto.fromClassifications(classifications, 5);
        logger.info("Classifications: " + classificationsDto);

        model.addAttribute("image", getImageAsBase64(imageFile));
        model.addAttribute("classifications", classificationsDto);
        return "result";
    }

    private String getImageAsBase64(MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            // Encode the byte array to Base64
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
