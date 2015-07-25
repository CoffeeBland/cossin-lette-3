package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.io.FileFilter;
import java.util.Comparator;

public class FileChooser extends Dialog {

    public interface ResultListener {
        boolean result(boolean success, FileHandle result);
    }

    protected final Skin skin;
    protected boolean fileNameEnabled;
    protected final TextField fileNameInput;
    protected final Label fileNameLabel;
    protected final FileHandle baseDir;
    protected final Label fileListLabel;
    protected final List<FileListItem> fileList;

    protected FileHandle currentDir;
    protected String result;

    protected ResultListener resultListener;

    protected Stage stage;

    protected final TextButton ok;
    protected final TextButton cancel;

    protected static final Comparator<FileListItem> dirListComparator = (file1, file2) ->
            file1.file.isDirectory() == file2.file.isDirectory() ? 0 :
            (file1.file.isDirectory() ? -1 : 1);

    protected FileFilter filter = pathname -> true;
    protected boolean directoryBrowsingEnabled = true ;

    public FileChooser(String title, final Skin skin, FileHandle baseDir) {
        super(title, skin);
        this.skin = skin;
        this.baseDir = baseDir;

        final Table content = getContentTable();
        content.top().left();

        fileListLabel = new Label("", skin);
        fileListLabel.setAlignment(Align.left);

        fileList = new List<>(skin);
        fileList.getSelection().setProgrammaticChangeEvents(false);

        fileNameInput = new TextField("", skin);
        fileNameLabel = new Label("File name:", skin);
        fileNameInput.setTextFieldListener((textField, c) -> result = textField.getText());

        ok = new TextButton("Ok", skin);
        button(ok, true);

        cancel = new TextButton("Cancel", skin);
        button(cancel, false);
        key(Keys.ENTER, true);
        key(Keys.ESCAPE, false);

        fileList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final FileListItem selected = fileList.getSelected();
                if (!selected.file.isDirectory()) {
                    result = selected.file.name();
                    fileNameInput.setText(result);
                }
            }
        });
    }

    protected void changeDirectory(FileHandle directory) {

        currentDir = directory;
        fileListLabel.setText(currentDir.path());

        final Array<FileListItem> items = new Array<>();

        final FileHandle[] list = directory.list(filter);
        for (final FileHandle handle : list) {
            items.add(new FileListItem(handle));
        }

        items.sort(dirListComparator);

        if (directory.file().getParentFile() != null) {
            items.insert(0, new FileListItem("..", directory.parent()));
        }

        fileList.setSelected(null);
        fileList.setItems(items);
    }

    public FileHandle getResult() {
        String path = currentDir.path() + "/";
        if (result != null && result.length() > 0) {
            path += result;
        }
        return new FileHandle(path);
    }
    public FileChooser setFilter(FileFilter filter) {
        this.filter = filter;
        return this;
    }
    public FileChooser setOkButtonText(String text) {
        this.ok.setText(text);
        return this;
    }
    public FileChooser setCancelButtonText(String text) {
        this.cancel.setText(text);
        return this;
    }
    public FileChooser setFileNameEnabled(boolean fileNameEnabled) {
        this.fileNameEnabled = fileNameEnabled;
        return this;
    }
    public FileChooser setResultListener(ResultListener result) {
        this.resultListener = result;
        return this;
    }
    public FileChooser disableDirectoryBrowsing() {
        this.directoryBrowsingEnabled = false;
        return this;

    }

    @Override
    public Dialog show(Stage stage, Action action) {
        final Table content = getContentTable();
        content.add(fileListLabel).top().left().expandX().fillX().row();
        content.add(new ScrollPane(fileList, skin)).size(300, 150).fill().expand().row();

        if (fileNameEnabled) {
            content.add(fileNameLabel).fillX().expandX().row();
            content.add(fileNameInput).fillX().expandX().row();
            stage.setKeyboardFocus(fileNameInput);
        }

        if(directoryBrowsingEnabled){
            fileList.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final FileListItem selected = fileList.getSelected();
                    if (selected.file.isDirectory()) {
                        changeDirectory(selected.file);
                    }
                }
            });
        }

        this.stage = stage;
        changeDirectory(baseDir);
        return super.show(stage, action);
    }

    public static FileChooser createSaveDialog(String title, final Skin skin, final FileHandle path) {
        return new FileChooser(title, skin, path) {
            @Override
            protected void result(Object object) {
                if (resultListener == null) return;
                if (!resultListener.result((boolean) object, getResult())) this.cancel();
            }
        }.setFileNameEnabled(true).setOkButtonText("Save");

    }
    public static FileChooser createLoadDialog(String title, final Skin skin, final FileHandle path) {
        return new FileChooser(title, skin, path) {
            @Override
            protected void result(Object object) {
                if (resultListener == null) return;
                resultListener.result((boolean) object, getResult());
            }
        }.setFileNameEnabled(false).setOkButtonText("Load");

    }
    public static FileChooser createPickDialog(String title, final Skin skin, final FileHandle path) {
        return new FileChooser(title, skin, path) {
            @Override
            protected void result(Object object) {
                if (resultListener == null) return;
                resultListener.result((boolean) object, getResult());
            }
        }.setOkButtonText("Select");
    }

    public static class FileListItem {
        public final FileHandle file;
        public final String name;

        public FileListItem(FileHandle file) {
            this.file = file;
            this.name = file.name();
        }
        public FileListItem(String name, FileHandle directory) {
            this.file = directory;
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }
    }
}