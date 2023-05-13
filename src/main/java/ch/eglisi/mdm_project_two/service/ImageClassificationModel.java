package ch.eglisi.mdm_project_two.service;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.DownloadUtils;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Component
public class ImageClassificationModel {
    // https://docs.djl.ai/jupyter/load_pytorch_model.html
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ImageClassificationModel.class);

    private final String modelRes = "build/pytorch_models/resnet18/";
    private final String modelUrl = "https://djl-ai.s3.amazonaws.com/mlrepo/model/cv/image_classification/ai/djl/pytorch/resnet/0.0.1/traced_resnet18.pt.gz";
    private final String synsetUrl = "https://djl-ai.s3.amazonaws.com/mlrepo/model/cv/image_classification/ai/djl/pytorch/synset.txt";

    private final ZooModel<Image, Classifications> model;

    public ImageClassificationModel() {
        loadModelResources();
        var translator = getTranslator();
        model = loadModel(translator);
        logger.info("Model loaded successfully");
    }

    private void loadModelResources() {
        try {
            // download pretrained model from the model zoo
            DownloadUtils.download(modelUrl, modelRes + "resnet18.pt", new ProgressBar());

            // save synset.txt with the classificator labels in the build/pytorch_models folder
            DownloadUtils.download(synsetUrl, modelRes + "synset.txt", new ProgressBar());
            logger.info("Model and synset.txt downloaded successfully");
        } catch (IOException e) {
            logger.error("Failed to download model", e);
        }
    }

    private Translator<Image, Classifications> getTranslator() {
        return ImageClassificationTranslator.builder()
                .addTransform(new Resize(256))
                .addTransform(new CenterCrop(224, 224))
                .addTransform(new ToTensor())
                .addTransform(new Normalize(
                        new float[]{0.485f, 0.456f, 0.406f},
                        new float[]{0.229f, 0.224f, 0.225f}))
                .optApplySoftmax(true)
                .build();
    }

    private ZooModel<Image, Classifications> loadModel(Translator<Image, Classifications> translator) {
        Criteria<Image, Classifications> criteria = Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optModelPath(Paths.get(modelRes))
                .optOption("mapLocation", "true") // this model requires mapLocation for GPU
                .optTranslator(translator)
                .optProgress(new ProgressBar()).build();
        try {
            logger.info("Loading model...");
            return criteria.loadModel();
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            logger.error("Failed to load model");
            throw new RuntimeException(e);
        }
    }

    private Image loadImage() {
        try {
            var img = ImageFactory.getInstance().fromUrl("https://raw.githubusercontent.com/pytorch/hub/master/images/dog.jpg");
            img.getWrappedImage();
            return img;
        } catch (IOException e) {
            logger.error("Failed to load image");
            throw new RuntimeException(e);
        }
    }

    public Classifications predict(MultipartFile img) {
        var image = transformImage(img);
        logger.info("Predicting...");
        try (Predictor<Image, Classifications> predictor = model.newPredictor()) {
            var classifications = predictor.predict(image);
            logger.info("classification: " + classifications);
            return classifications;
        } catch (TranslateException e) {
            logger.error("Failed to predict");
            throw new RuntimeException(e);
        }
    }

    private Image transformImage(MultipartFile img) {
        try {
            return ImageFactory.getInstance().fromInputStream(img.getInputStream());
        } catch (IOException e) {
            logger.error("Failed to load image");
            throw new RuntimeException(e);
        }
    }
}
