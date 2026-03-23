package indi.wenyan.client.gui.code_editor.backend.interfaces;

// Since we don't have generic player base container synchronization, like menu
// so need the context to provide the synchronization
public interface CodeEditorBackendSynchronizer extends ContentSynchronizer, TitleSynchronizer, OutputSynchronizer {
}
