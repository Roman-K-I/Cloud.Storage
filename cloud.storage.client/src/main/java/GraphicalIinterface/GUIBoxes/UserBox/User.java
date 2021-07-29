package GraphicalIinterface.GUIBoxes.UserBox;

import Configuration.Config;
import FileManager.*;
import Archive.*;
import Files.SearchFiles;
import GraphicalIinterface.CommonСlassesGUI.ListFiles.ButtonsSort;
import GraphicalIinterface.CommonСlassesGUI.ListFiles.FileInfo;
import Files.FileBuffer;
import GraphicalIinterface.MainUserInterface.ManagerBox;
import GraphicalIinterface.CommonСlassesGUI.ListFiles.ListFiles;
import GraphicalIinterface.CommonСlassesGUI.Navigation.Navigation;
import GraphicalIinterface.MainUserInterface.*;
import GraphicalIinterface.CommonСlassesGUI.ShowFiles.ShowImg;
import GraphicalIinterface.CommonСlassesGUI.ShowFiles.ShowMedia;
import GraphicalIinterface.CommonСlassesGUI.ShowFiles.ShowText;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.*;
import java.util.List;

public class User extends ManagerBox {

    private final MainBox mainBox;
    private final FileBuffer fileBuffer;

    private String root;
    private String currentDirectory = Config.USER_START_CUR_DIR;

    private final Navigation navigation;
    private final ListFiles listFiles;
    private Tab tab;
    private ShowText showText;
    private ShowImg showImg;
    private ShowMedia showMedia;
    private final FileManager fileManager;

    private final boolean server;


    public User (MainBox mainBox) {

        this.mainBox = mainBox;
        this.fileBuffer = mainBox.getFileBuffer();
        this.server = false;
        this.fileManager = new FileManager();
        this.root = getRoots()[0];

        this.navigation = new Navigation(this);
        this.listFiles = new ListFiles(this, mainBox.getMouseManager());

        VBox mainUserVBox = new VBox();
        mainUserVBox.setSpacing(5);

        mainUserVBox.getChildren().addAll(navigation, listFiles);

        getChildren().add(mainUserVBox);

        HBox.setHgrow(mainUserVBox, Priority.ALWAYS);
        VBox.setVgrow(mainUserVBox, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        setAlignment(Pos.CENTER);

        mainBox.addUser(this);

        getStyleClass().add("box-manager");
        setPadding(new Insets(2,2,2,2));

        update();

    }


    @Override
    public void closeBox(){
        mainBox.removeUser(this);
    }

    @Override
    public boolean isServer () {
        return server;
    }

    //    Поиск файла или директории
    @Override
    public void searchFile(String cmd){

        List<FileInfo> fileInfoList = new SearchFiles().searchFile(getCurrentAbsolutePath(), cmd);
        if (fileInfoList != null ) listFiles.update(fileInfoList);
    }

//        Открытие предыдущей директории пользователя
    @Override
    public void currentDirectoryDown () {
        currentDirectory = fileManager.getParentCurrentDirectory( currentDirectory);
        update();
    }


    //    Открытие новой директории или файла
    @Override
    public void currentDirectoryUp(String nameFile){

        if (fileManager.checkDirectory(getCurrentAbsolutePath() + File.separator+ nameFile)) {
            currentDirectory += File.separator + nameFile;
            update();
        }
    }

    @Override
    public boolean isCurrentDirectory(){
        return currentDirectory.length() > 0;
    }

    @Override
    public void currentDirectoryHome(){
        currentDirectory = "";
        update();
    }

    @Override
    public String getCurrentAbsolutePath() {
        return root + File.separator + currentDirectory;
    }


    //    Устатовить текущю директорию пользователя
    @Override
    public void setCurrentDirectory (String dir) {

        if (fileManager.checkDirectory(root + dir)) {
            currentDirectory = new File(dir).toString();
            update();
        }

    }

    @Override
    public void showTextFile (FileInfo fileInfo) {
        closeShowBoxes();
        showText = new ShowText(fileInfo.getFile(), null, false, true);
        add(showText);
    }

    @Override
    public void showImgFile (FileInfo fileInfo) {
        closeShowBoxes();
        showImg = new ShowImg(fileInfo.getFile(), false);
        add(showImg);
    }

    @Override
    public void showMediaFile (FileInfo fileInfo) {
        closeShowBoxes();
        showMedia = new ShowMedia(fileInfo.getFile(), false);
        add(showMedia);
    }

    private void closeShowBoxes(){

        if (showText != null){
            showText.close();
        }
        if (showImg != null){
            showImg.close();
        }
        if (showMedia != null){
            showMedia.close();
        }
    }

    private void add(Node node){
        Platform.runLater(()->{
            getChildren().add(node);
        });
    }

    //    Создание нового файла
    @Override
    public void createFile(String fileName) {
        fileManager.createFile(getCurrentAbsolutePath()+ File.separator + fileName);
        mainBox.updateUser(getCurrentAbsolutePath());
    }

    //  Создание новой директории
    @Override
    public void createDirectory (String path) {
        fileManager.createDir(getCurrentAbsolutePath()+ File.separator + path);
        mainBox.updateUser(getCurrentAbsolutePath());
    }

    @Override
    public void addNewBox (FileInfo fileInfo) {
        listFiles.add(fileInfo);
    }

    //   Удаление файла или директории
    @Override
    public void delete(List<FileInfo> bufListFileInfo) {
        fileManager.deleteFiles(getArrayFilesFromListFileInfo(bufListFileInfo));
        mainBox.updateUser(getCurrentAbsolutePath());
    }

    //   Переименовать файл или директорию
    @Override
    public void renameFile (FileInfo fileInfo, String name) {
        fileManager.rename(fileInfo.getFile(), name);
        mainBox.updateUser(getCurrentAbsolutePath());
    }

    @Override
    public void update(){
        listFiles.update();
        navigation.update();
        updateTab();
    }

    @Override
    public String getCurrentDirectory () {
        return new File(currentDirectory).toString();
    }

    @Override
    public void setCurRoot(int i){
        this.root = getRoots()[i];
    }

    @Override
    public String getCurRoot(){
        return  this.root;
    }

    @Override
    public String[] getRoots(){
        return fileManager.getListRootsSystem();
    }

    @Override
    public void copyFile(ManagerBox managerBox, List<FileInfo> bufCopy) {
        for(FileInfo fileIf: bufCopy) {
            fileManager.copyFiles(managerBox.getCurrentAbsolutePath(), fileIf.getFile());
        }
        mainBox.updateUser(managerBox.getCurrentAbsolutePath(), getCurrentAbsolutePath());
    }

    @Override
    public void zip(List<FileInfo> fileInfoList){
        for (FileInfo fileInfo: fileInfoList) {
            new ArhZipFile().getZipFiles(fileInfo.getFile());
        }
        mainBox.updateUser(getCurrentAbsolutePath());
    }


    @Override
    public void unZip(List<FileInfo> fileInfoList){
        File[] files = getArrayFilesFromListFileInfo(fileInfoList);
        new ArhUnZipFile().getUnzipFiles(getCurrentAbsolutePath(), files);
        mainBox.updateUser(getCurrentAbsolutePath());
    }

    private File[] getArrayFilesFromListFileInfo (List<FileInfo> fileInfoList) {
        File[] files = new File[fileInfoList.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = fileInfoList.get(i).getFile();
        }
        return files;
    }

    @Override
    public FileBuffer getFileBuffer(){
        return this.fileBuffer;
    }

    public boolean checkUpload(){
        return mainBox.checkUpload();
    }

    public boolean checkDownload(){
        return mainBox.checkDownload();
    }

    @Override
    public String infoToStr(){
        String info  = new File(getCurrentAbsolutePath()).getName();
        if (info.length() == 0) info = root;
        return  info;
    }

    @Override
    public void addTab (Tab tab) {
        this.tab = tab;
    }

    private void updateTab(){
        if (tab != null){
            Platform.runLater(()->{
                tab.setText(infoToStr());
            });
        }
    }

    @Override
    public TextField getNode(){
        return navigation.getNode();
    }

    @Override
    public ListFiles getListFiles(){
        return listFiles;
    }
}
