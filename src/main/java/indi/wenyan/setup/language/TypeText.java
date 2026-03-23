package indi.wenyan.setup.language;

public enum TypeText implements ILocalizationEnum {
    RunningState,
    CheckerObject,
    Checker7Map,
    Position,
    PositionType,
    Block,
    Itemslot,
    Player,
    Vec3,
    Vec3ObjectType,
    Entity
    ;

    @Override
    public String getTranslationKey() {
        return "type.wenyan_programming." + name();
    }
}
