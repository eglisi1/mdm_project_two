package ch.eglisi.mdm_project_two.model;

import ai.djl.modality.Classifications;

import java.util.ArrayList;
import java.util.List;

public record ClassificationDto(String className, double probability) {
    public ClassificationDto(String className, double probability) {
        this.className = defineClassName(className);
        this.probability = probability;
    }

    private String defineClassName(String s) {
        var strings = s.split(" ");
        return strings[strings.length - 1];
    }

    public static List<ClassificationDto> fromClassifications(Classifications classifications, Integer topK) {
        var topClassifications = classifications.topK(topK == null ? 3 : topK);
        List<ClassificationDto> classificationDtos = new ArrayList<>();
        topClassifications.forEach(classification -> classificationDtos.add(new ClassificationDto(classification.getClassName(), classification.getProbability())));
        return classificationDtos;
    }
}
