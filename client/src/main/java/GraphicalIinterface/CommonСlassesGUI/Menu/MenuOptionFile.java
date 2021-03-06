package GraphicalIinterface.CommonСlassesGUI.Menu;

import GraphicalIinterface.CommonСlassesGUI.ListFiles.FileInfo;
import GraphicalIinterface.CommonСlassesGUI.DialogStage.QuestionDelete;
import GraphicalIinterface.GUIBoxes.CloudBox.Cloud;
import GraphicalIinterface.MainUserInterface.MainBox;
import GraphicalIinterface.MainUserInterface.ManagerBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.awt.*;

public class MenuOptionFile extends ContextMenu {

    private double x;
    private double y;

    public MenuOptionFile (ManagerBox managerBox, FileInfo fileInfo) {

        if (fileInfo != null) {

            if (fileInfo.isDirectory()){

                MenuItem itemOpenDir = new MenuItem("Открыть");

                itemOpenDir.setOnAction((event) -> {

                    managerBox.currentDirectoryUp(fileInfo.getFullName ());
                });
                getItems().add(itemOpenDir);

            } else {

                MenuItem itemReadText = new MenuItem("Открыть как текст");

                itemReadText.setOnAction((event) -> {

                    managerBox.showTextFile(fileInfo);
                });

                MenuItem itemReadImg = new MenuItem("Открыть как изображение");

                itemReadImg.setOnAction((event) -> {

                    managerBox.showImgFile(fileInfo);
                });

                MenuItem itemMedia = new MenuItem("Открыть как видео");

                itemMedia.setOnAction((event) -> {

                    managerBox.showMediaFile(fileInfo);
                });

                getItems().addAll(itemReadText, itemReadImg, itemMedia);
            }

        }

        if (managerBox.isCurrentDirectory()){

            MenuItem itemBack = new MenuItem("Назад");

            itemBack.setOnAction((event) -> {

                managerBox.currentDirectoryDown();
            });

            getItems().add(itemBack);

        }


        if (MainBox.fileBuffer.checkBuf()) {

            if(MainBox.fileBuffer.checkBuf()) {

                MenuItem itemUpload = new MenuItem("Оправить в Cloud storage");

                itemUpload.setOnAction((event) -> {
                    MainBox.fileBuffer.upload(null);
                });


                MenuItem itemDownload = new MenuItem("Скачать файл");

                itemDownload.setOnAction((event) -> {

                    MainBox.fileBuffer.download((Cloud) managerBox, null);
                });



                MenuItem itemRename = new MenuItem("Переименовать");

                itemRename.setOnAction((event) -> {

                    fileInfo.renameFile();

                    fileInfo.getTextField().setOnAction((a) -> {
                        managerBox.renameFile(fileInfo, fileInfo.getTextField().getText());
                    });
                });

                MenuItem itemDelete = new MenuItem("Удалить");

                itemDelete.setOnAction((event) -> {
                    new QuestionDelete(managerBox, MainBox.fileBuffer.getBufListFileInfo(), x, y);
                });

                MenuItem itemCopy = new MenuItem("Копировать");
                itemCopy.setOnAction((event) -> {
                    MainBox.fileBuffer.updateBufferList(false);
                });

                MenuItem itemCut = new MenuItem("Вырезать");
                itemCut.setOnAction((event) -> {
                    MainBox.fileBuffer.updateBufferList(true);
                });

                getItems().addAll(itemUpload, itemDownload, itemRename, itemDelete, itemCopy, itemCut);
            }
        }

        if (fileInfo != null) {

                MenuItem itemInsert = new MenuItem("Вставить");

                itemInsert.setOnAction((event) -> {
                    MainBox.fileBuffer.copyBuf(managerBox);

                });

                getItems().add(itemInsert);
        }

        if (fileInfo != null){

            MenuItem itemZip = new MenuItem();

            if (fileInfo.isArchive()){

                itemZip.setText("Разархивировать");

                itemZip.setOnAction((event)->{

                    managerBox.unZip(MainBox.fileBuffer.getBufListFileInfo());
                });
            } else {

                itemZip.setText("Архивировать");

                itemZip.setOnAction((event)->{

                    managerBox.zip(MainBox.fileBuffer.getBufListFileInfo());
                });

            }

            getItems().add(itemZip);
        }

        MenuItem itemCreateDir = new MenuItem("Создать папку");

        itemCreateDir.setOnAction((event)->{

            FileInfo newFileInfo = new FileInfo(true);

            newFileInfo.getTextField().setOnAction((a) ->{

                managerBox.createDirectory(newFileInfo.getTextField().getText());
            });

            managerBox.getListFiles().getFileInfoListView().getItems().add(newFileInfo);
        });

        MenuItem itemCreateFile = new MenuItem("Создать файл");

        itemCreateFile.setOnAction((event)->{

            FileInfo newFileInfo = new FileInfo(false);

            newFileInfo.getTextField().setOnAction((a) ->{

                managerBox.createFile(newFileInfo.getTextField().getText());
            });

            managerBox.getListFiles().getFileInfoListView().getItems().add(newFileInfo);
        });

        getItems().addAll(itemCreateDir, itemCreateFile);

        x = MouseInfo.getPointerInfo().getLocation().getX();
        y = MouseInfo.getPointerInfo().getLocation().getY();

        managerBox.getNode().setContextMenu(this);
        show (managerBox.getNode(), x,y);
    }
}
