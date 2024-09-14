package indi.wenyan.gui;

import indi.wenyan.block.BlockRunner;

public class BlockRunnerScreen extends RunnerScreen {
    public BlockRunnerScreen(BlockRunner blockRunner) {
        super(blockRunner.pages);
    }

    @Override
    protected void saveChanges() {}

    @Override
    protected boolean bookKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.getFocused() != null && this.getFocused().charTyped(codePoint, modifiers);
    }
}
