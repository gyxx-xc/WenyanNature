package indi.wenyan.client.gui.code_editor.backend.interfaces;

public interface RunnerDebugSynchronizer extends OutputSynchronizer {
    int getContextStart();

    int getContextEnd();
}
