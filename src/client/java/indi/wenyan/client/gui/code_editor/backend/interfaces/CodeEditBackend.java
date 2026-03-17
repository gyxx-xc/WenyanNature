package indi.wenyan.client.gui.code_editor.backend.interfaces;

import indi.wenyan.client.gui.code_editor.backend.behaviour.CodeField;
import indi.wenyan.client.gui.code_editor.backend.behaviour.SnippetSet;

import java.util.List;

public interface CodeEditBackend {
    String getContent();

    List<CodeField.Placeholder> getPlaceholders();

    // following part need to communicated
    void insertText(String text);

    void setCursor(int cursor);

    void setSelectCursor(int selectCursor);

    int getCursor();

    int getSelectCursor();

    List<SnippetSet> getCurSnippets();

    void setCurSnippets(List<SnippetSet> curSnippets);

    void setCursorListener(Runnable cursorListener);

    void setValueListener(Runnable valueListener);
}
