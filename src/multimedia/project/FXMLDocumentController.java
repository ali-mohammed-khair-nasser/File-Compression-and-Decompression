package multimedia.project;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane ApplicationStage;

    @FXML
    private Pane MainPane, CompressDecompressPane, SelectAlgorithmPane, OutputPane, LoadingPane;

    @FXML
    private AnchorPane MainUploadViewPane, OutputScrollView;

    @FXML
    private ToggleGroup SelectCompressingType, SelectAlgorithmType;

    @FXML
    private Text UploadingText, OutputPaneText;

    @FXML
    private Button BrowseButton;

    @FXML
    private static final FadeTransition fadeIn = new FadeTransition();
    private static final FadeTransition fadeOut = new FadeTransition();

    private double xOffset = 0, yOffset = 0, yOffsetPos = 0, anchorPaneHeight = 0;
    
    private String FileName, FileSize, FileType, ButtonID = "Compress_Files", AlgorithmType = null, CompressingType = "Files";

    private final List<File> UploadedFiles = new ArrayList<>();
    private final List<File> ConvertedFiles = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        draggableStage();
        DisplayData(MainUploadViewPane, UploadedFiles);
    }

    @FXML // Close the application on close icon clicked
    private void closeApp() {
        System.exit(0);
    }

    @FXML // Close uploading stage and back to home
    private void backStage() {
        hideTransition(CompressDecompressPane);
        hideTransition(SelectAlgorithmPane);
        showTransition(MainPane);
        UploadedFiles.clear();
        ConvertedFiles.clear();
    }

    @FXML // Close uploading stage and back to home
    private void backToHome() {
        hideTransition(CompressDecompressPane);
        hideTransition(LoadingPane);
        hideTransition(OutputPane);
        showTransition(MainPane);
        UploadedFiles.clear();
        ConvertedFiles.clear();
    }

    // Make the window of the application draggable
    private void draggableStage() {
        ApplicationStage.setOnMousePressed((event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        ApplicationStage.setOnMouseDragged((event) -> {
            MultimediaProject.stage.setX(event.getScreenX() - xOffset);
            MultimediaProject.stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML // Get pressed button ID to show the right panel to the user
    private void getButtonID(MouseEvent event) throws NoSuchFieldException {
        String ButtonInfo = event.getTarget().toString();
        String[] SplitedButtonInfo = ButtonInfo.split(",");
        String[] Splited2ButtonInfoParts = SplitedButtonInfo[0].split("=");
        ButtonID = Splited2ButtonInfoParts[1];

        switch (ButtonID) {
            case "Compress_Files": {
                anchorPaneHeight = 0;
                yOffsetPos = 0;
                MainUploadViewPane.setPrefHeight(anchorPaneHeight);
                MainUploadViewPane.getChildren().clear();
                showTransition(CompressDecompressPane);
                UploadingText.setText(CompressingType + " upload");
                OutputPaneText.setText("Compressed " + CompressingType.toLowerCase());
                if ("Files".equals(CompressingType)) {
                    BrowseButton.setOnAction((ActionEvent event1) -> {
                        try { UploadFiles(); } catch (IOException ex) { Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex); }
                    });
                } else {
                    BrowseButton.setOnAction((ActionEvent event1) -> {
                        try { UploadDirectories(); } catch (IOException ex) { Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex); }
                    });
                }
                System.out.println("Compressing " + CompressingType + " using " + AlgorithmType + " algorithm.");
            }
            break;
            case "Decompress_Files": {
                CompressingType = "Files";
                anchorPaneHeight = 0;
                yOffsetPos = 0;
                MainUploadViewPane.setPrefHeight(anchorPaneHeight);
                MainUploadViewPane.getChildren().clear();
                showTransition(CompressDecompressPane);
                UploadingText.setText("Files upload");
                OutputPaneText.setText("Decompressed files");
                if ("Files".equals(CompressingType)) {
                    BrowseButton.setOnAction((ActionEvent event1) -> {
                        try { UploadFiles(); } catch (IOException ex) { Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex); }
                    });
                }
                System.out.println("Decompressing " + CompressingType + " using " + AlgorithmType + " algorithm.");
            }
            break;
            case "Select_Algorithm": {
                anchorPaneHeight = 0;
                yOffsetPos = 0;
                MainUploadViewPane.setPrefHeight(anchorPaneHeight);
                MainUploadViewPane.getChildren().clear();
                showTransition(SelectAlgorithmPane);
            }
            break;
        }
    }

    // Upload file to compress or decompress it using the algorithms
    public void UploadFiles() throws IOException {
        Stage stage = new Stage();
        FileChooser chooseFile = new FileChooser();
        chooseFile.setTitle("Upload Files");
        List<File> selectedFiles = chooseFile.showOpenMultipleDialog(stage);
        if (selectedFiles == null) { return; }
        for (File file : selectedFiles) { if (!UploadedFiles.contains(file)) { UploadedFiles.add(file); } }
        DisplayData(MainUploadViewPane, UploadedFiles);
    }

    // Upload file to compress or decompress it using the algorithms
    public void UploadDirectories() throws IOException {
        Stage stage = new Stage();
        DirectoryChooser chooseDirectory = new DirectoryChooser();
        chooseDirectory.setTitle("Upload Directories");
        File selectedDirectory = chooseDirectory.showDialog(stage);
        if (selectedDirectory == null) { return; }
        if (!UploadedFiles.contains(selectedDirectory)) { UploadedFiles.add(selectedDirectory); }
        DisplayData(MainUploadViewPane, UploadedFiles);
    }

    @FXML
    private void dragAndDropFiles(DragEvent event) { event.acceptTransferModes(TransferMode.ANY); }

    @FXML
    private void handelDragAndDropFiles(DragEvent event) {
        for (File file : event.getDragboard().getFiles()) { if (!UploadedFiles.contains(file)) { UploadedFiles.add(file); } }
        DisplayData(MainUploadViewPane, UploadedFiles);
    }

    private void DisplayData(AnchorPane anchorPane, List<File> file) {
        anchorPaneHeight = 0;
        yOffsetPos = 0;
        anchorPane.setPrefHeight(anchorPaneHeight);
        anchorPane.getChildren().clear();

        for (int i = 0; i < file.size(); i++) {
            FileName = file.get(i).getName();
            FileType = getFileExtension(file.get(i)).toUpperCase();
            FileSize = FileSizeConvert(file.get(i).length());

            if ("".equals(FileType) || FileType == null) {
                FileType = "Fold".toUpperCase();
                FileSize = FileSizeConvert(folderSize(file.get(i)));
            }

            Pane FilePane = new Pane();
            Pane ImagePane = new Pane();
            FilePane.setPrefSize(632, 103);
            if (file.size() > 3) { FilePane.setPrefSize(617, 103); }
            FilePane.setLayoutY(yOffsetPos);

            // File image
            ImagePane.setPrefSize(48, 65);
            ImagePane.setLayoutX(14);
            ImagePane.setLayoutY(19);
            ImagePane.setStyle("-fx-background-color: transparent; -fx-background-image: url('GUI/FileIcon.png');");
            if ("FOLD".equals(FileType)) { ImagePane.setStyle("-fx-background-color: transparent; -fx-background-image: url('GUI/FolderIcon.png');"); }

            // File name
            Text FileFullName = new Text(FileName);
            FileFullName.setLayoutX(75);
            FileFullName.setLayoutY(46);
            FileFullName.setFill(Color.valueOf("#1F2532"));
            FileFullName.setFont(Font.font("Segoe UI Semibold", FontWeight.NORMAL, 17));

            // File extention ( In image )
            Text FileExtention = new Text(FileType);
            FileExtention.setLayoutX(3);
            FileExtention.setLayoutY(56);
            FileExtention.setFill(Color.valueOf("#8c96a8"));
            FileExtention.setWrappingWidth(38.66665511120664);
            FileExtention.setTextAlignment(TextAlignment.RIGHT);
            FileExtention.setFont(Font.font("Segoe UI Semibold", FontWeight.NORMAL, 13));

            // File information
            Text FileInformation = new Text("File size: " + FileSize + " - File type: " + FileType);
            if ("Directories".equals(CompressingType)) { FileInformation.setText("Directory size: " + FileSize); }
            FileInformation.setLayoutX(75);
            FileInformation.setLayoutY(71);
            FileInformation.setFill(Color.valueOf("#8c96a8"));
            FileInformation.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));

            // Add them to generated pane then to main anchor pane
            ImagePane.getChildren().add(FileExtention);
            FilePane.getChildren().add(ImagePane);
            FilePane.getChildren().add(FileFullName);
            FilePane.getChildren().add(FileInformation);
            anchorPane.getChildren().add(FilePane);

            yOffsetPos += 105;
            anchorPaneHeight += 105;
            anchorPane.setPrefHeight(anchorPaneHeight);
        }
    }

    @FXML
    private void StartAction() throws IOException {
        showTransition(LoadingPane);
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                switch (ButtonID) {
                    case "Compress_Files": {
                        if ("Shannon-fano".equals(AlgorithmType)) {
                            ShannonFanoCompression compression = new ShannonFanoCompression();
                            for (int i = 0; i < UploadedFiles.size(); i++) {
                                File file = UploadedFiles.get(i);
                                if ("Files".equals(CompressingType) && file != null) {
                                    compression.compressSingleFile(file.getAbsolutePath(), file.getAbsolutePath() + ".shf");
                                    System.out.println(compression.getCompressionRatio(file.getAbsolutePath(), file.getAbsolutePath() + ".shf") + "       " + "compression ratio");
                                } else if (file != null) { compression.compressDir(file.getAbsolutePath(), file.getAbsolutePath() + ".shf"); }
                            }
                            Platform.runLater(() -> {
                                showTransition(OutputPane);
                                for (int i = 0; i < UploadedFiles.size(); i++) { ConvertedFiles.add(new File(UploadedFiles.get(i).getAbsolutePath() + ".shf")); }
                                DisplayData(OutputScrollView, ConvertedFiles);
                            });
                        } else if ("LZW".equals(AlgorithmType)) {
                            ShannonFanoCompression compression = new ShannonFanoCompression();
                            for (int i = 0; i < UploadedFiles.size(); i++) {
                                File file = UploadedFiles.get(i);
                                if ("Files".equals(CompressingType) && file != null) {
                                    compression.compressSingleFile(file.getAbsolutePath(), file.getAbsolutePath() + ".lzw");
                                    System.out.println(compression.getCompressionRatio(file.getAbsolutePath(), file.getAbsolutePath() + ".lzw") + "       " + "compression ratio");
                                } else if (file != null) { compression.compressDir(file.getAbsolutePath(), file.getAbsolutePath() + ".lzw"); }
                            }
                            Platform.runLater(() -> {
                                showTransition(OutputPane);
                                for (int i = 0; i < UploadedFiles.size(); i++) { ConvertedFiles.add(new File(UploadedFiles.get(i).getAbsolutePath() + ".lzw")); }
                                DisplayData(OutputScrollView, ConvertedFiles);
                            });
                        } else {
                            ImageCompression compression = new ImageCompression();
                            for (int i = 0; i < UploadedFiles.size(); i++) {
                                File file = UploadedFiles.get(i);
                                if ("Files".equals(CompressingType) && file != null) {
                                    try {
                                        compression.CompressImage(file.getAbsolutePath());
                                        System.out.println(compression.getCompressionRatio(file.getAbsolutePath(), file.getParent()) + "       " + "compression ratio");
                                    } catch (IOException ex) { Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex); }
                                }
                            }
                            Platform.runLater(() -> {
                                showTransition(OutputPane);
                                ConvertedFiles.add(UploadedFiles.get(0));
                                for (int i = 0; i < UploadedFiles.size(); i++) {
                                    String name=UploadedFiles.get(i).getName();
                                    int n=name.lastIndexOf(".");
                                    String n1=name.substring(0,n);                
                                    ConvertedFiles.add(new File(UploadedFiles.get(i).getParent()+"\\"+n1+"compress.jpg"));
                                }
                                DisplayData(OutputScrollView, ConvertedFiles);
                            });
                        }
                    }
                    break;
                    case "Decompress_Files": {
                        if ("Shannon-fano".equals(AlgorithmType)) {
                            ShannonFanoCompression compression = new ShannonFanoCompression();
                            for (int i = 0; i < UploadedFiles.size(); i++) {
                                File file = UploadedFiles.get(i);
                                if (file != null) { compression.decompress(file.getAbsolutePath(), file.getParent()); }
                            }
                            Platform.runLater(() -> {
                                showTransition(OutputPane);
                                for (int i = 0; i < UploadedFiles.size(); i++) { ConvertedFiles.add(new File(UploadedFiles.get(i).getAbsolutePath().replace(".shf", ""))); }
                                DisplayData(OutputScrollView, ConvertedFiles);
                            });
                        } else {
                            ShannonFanoCompression compression = new ShannonFanoCompression();
                            for (int i = 0; i < UploadedFiles.size(); i++) {
                                File file = UploadedFiles.get(i);
                                if (file != null) { compression.decompress(file.getAbsolutePath(), file.getParent()); }
                            }
                            Platform.runLater(() -> {
                                showTransition(OutputPane);
                                for (int i = 0; i < UploadedFiles.size(); i++) { ConvertedFiles.add(new File(UploadedFiles.get(i).getAbsolutePath() + ".lzw")); }
                                DisplayData(OutputScrollView, ConvertedFiles);
                            });
                        }
                    }
                    break;
                }
            }
        }, 100);
    }

    // Get file type ( Name extention )
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) { return fileName.substring(fileName.lastIndexOf(".") + 1); } else { return ""; }
    }

    // Get folder size in bytes
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) { length += file.length(); } else { length += folderSize(file); }
        }
        return length;
    }

    // Convert file size from bytes to human readable units like KB MB GB ...etc
    private static String FileSizeConvert(long bytes) {
        if (-1000 < bytes && bytes < 1000) { return bytes + " B"; }
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    // Start fade in animation function
    private void showTransition(Pane mainPane) {
        fadeIn.setNode(mainPane);                           // The node that we want to show it
        fadeIn.setDuration(Duration.millis(200));           // Show the node in a half minute
        fadeIn.setFromValue(0.0);                           // Start Showing from 0 opacity
        fadeIn.setToValue(1.0);                             // End Showing in 1 opacity
        mainPane.setVisible(true);                          // Make the node visible after transition
        fadeIn.play();                                      // Finish and play the function
    }
    // End fade in animation function

    // Start fade out animation function
    private void hideTransition(Pane mainPane) {
        fadeOut.setNode(mainPane);                          // The node that we want to show it
        fadeOut.setDuration(Duration.millis(200));          // Show the node in a half minute
        fadeOut.setFromValue(1.0);                          // Start Showing from 0 opacity
        fadeOut.setToValue(0.0);                            // End Showing in 1 opacity
        mainPane.setVisible(false);                         // Make the node visible after transition
        fadeOut.play();                                     // Finish and play the function
    }
    // End fade out animation function

    @FXML // Make select on radio button of clicked algorithm pane
    private void selectAlgorithm(MouseEvent event) {
        Node parent = ((Node) event.getTarget()).getParent();
        Pane newPane = (Pane) parent;
        newPane = (newPane.getChildren().get(0) instanceof RadioButton) ? (Pane) parent : (Pane) event.getTarget();
        RadioButton SelectedAlgorithmRadio = (RadioButton) newPane.getChildren().get(0);
        SelectedAlgorithmRadio.setSelected(true);
    }

    @FXML
    private void getOptions() {
        RadioButton selectedCompressingRadioButton = (RadioButton) SelectCompressingType.getSelectedToggle();
        RadioButton selectedAlgorithmRadioButton = (RadioButton) SelectAlgorithmType.getSelectedToggle();
        CompressingType = selectedCompressingRadioButton.getText();
        String[] SplitedAlgorithmText = selectedAlgorithmRadioButton.getText().split(" ");
        AlgorithmType = SplitedAlgorithmText[SplitedAlgorithmText.length - 1];
        hideTransition(SelectAlgorithmPane);
        showTransition(MainPane);
    }
}
