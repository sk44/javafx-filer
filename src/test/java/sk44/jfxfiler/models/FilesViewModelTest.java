package sk44.jfxfiler.models;

import java.io.File;
import javafx.scene.control.TableView;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * Created by sk on 2/25/14.
 */
public class FilesViewModelTest {

    @Test
    public void testSelectNext() throws Exception {
        FilesViewModel sut = new FilesViewModel();
        sut.getFiles().addAll(
            new PathModel(new File("/foo/bar/buzz").toPath()),
            new PathModel(new File("/foo/bar/aaa").toPath()),
            new PathModel(new File("/foo/bar/bbb").toPath())
        );
        TableView<PathModel> tv = new TableView<>(sut.getFiles());
        sut.focusModelProperty().set(tv.focusModelProperty().get());
        sut.selectionModelProperty().set(tv.selectionModelProperty().get());
        sut.selectNext("a");

        assertThat(sut.getFocusedIndex(), is(1));

    }
}
