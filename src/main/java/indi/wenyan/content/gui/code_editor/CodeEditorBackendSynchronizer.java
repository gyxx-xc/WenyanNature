package indi.wenyan.content.gui.code_editor;

// Since we don't have generic player base container synchronization, like menu
// so need the context to provide the synchronization
public interface CodeEditorBackendSynchronizer {
    // STUB: change to insert text in future
    void sendContent(String content);
    String getContent();

    void sendTitle(String title);
    String getTitle();
}
