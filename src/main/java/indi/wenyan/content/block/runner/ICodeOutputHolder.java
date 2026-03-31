package indi.wenyan.content.block.runner;

public interface ICodeOutputHolder extends ICodeHolder, IOutputAccepter {
    boolean isOutputChanged();
}
