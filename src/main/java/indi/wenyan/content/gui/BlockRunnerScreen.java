package indi.wenyan.content.gui;

import indi.wenyan.content.block.BlockRunner;

public class BlockRunnerScreen extends RunnerScreen {
    public BlockRunnerScreen(BlockRunner blockRunner) {
        super(blockRunner.pages);
    }
    @Override
    protected void saveChanges() {}
    @Override
    protected boolean bookKeyPressed(int keyCode, int scanCode, int modifiers) {return false;}
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return getFocused() != null && getFocused().charTyped(codePoint, modifiers);
    }
}
