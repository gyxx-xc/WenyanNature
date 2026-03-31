package indi.wenyan.content.block;

public interface ICodeHolder extends IRenamable {
    void setCode(String code);

    void setPlatformName(String platformName);

    String getPlatformName();

    String getCode();

    @Override
    default void setName(String name) {
        setPlatformName(name);
    }
}
