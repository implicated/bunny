package bunny.terminal;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenTerminalAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        try {
            final String directory = getDirectory(event);
            new ProcessBuilder(Stream.of("open", directory, "-a", "iTerm").collect(Collectors.toList()))
                    .directory(new File(directory))
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute the command!", e);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = getEventProject(event);
        event.getPresentation().setEnabledAndVisible(
                project != null && getSelectedFile(event) != null);
    }

    @NotNull
    protected String getDirectory(AnActionEvent event) {
        VirtualFile directory = getSelectedDirectory(event);
        if (directory == null) {
            return System.getProperty("user.home");
        }
        return directory.getPath();
    }


    @Nullable
    private VirtualFile getSelectedDirectory(@NotNull AnActionEvent event) {
        VirtualFile file = getSelectedFile(event);
        return file != null
                ? file.isDirectory() ? file : file.getParent()
                : null;
    }

    @Nullable
    private static VirtualFile getSelectedFile(@NotNull AnActionEvent event) {
        return RevealFileAction.findLocalFile(event.getData(CommonDataKeys.VIRTUAL_FILE));
    }
}
