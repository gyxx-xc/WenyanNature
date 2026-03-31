package indi.wenyan.content.block.writing_block;

import indi.wenyan.content.block.ICodeHolder;

public enum DummyCodeHolder implements ICodeHolder {
    INSTANCE;

    @Override
    public void setCode(String code) {
    }

    @Override
    public void setPlatformName(String platformName) {
    }

    @Override
    public String getPlatformName() {
        return "";
    }

    @Override
    public String getCode() {
        return "";
    }
}
