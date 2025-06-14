package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className EntityFinderHandler
 * @Description 寻 使用继承了Entity的泛型作为单格范围内中心的实体的类
 * @date 2025/6/6 15:56
 */
public class EntityFinderHandler<T extends Entity> implements JavacallHandler {
    private final Level level;
    public static final WenyanType[] ARGS_TYPE = {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};
    private Class<T> entityClass=(Class<T>)Entity.class;

    public EntityFinderHandler(Level level) {
        this.level = level;
    }
    //继承了 寻 的类专用方法
    public EntityFinderHandler(Level level, Class<T> entityClass) {
        this.level = level;
        this.entityClass = entityClass;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        double x = Double.parseDouble(String.valueOf(args[0]));
        double y = Double.parseDouble(String.valueOf(args[1]));
        double z = Double.parseDouble(String.valueOf(args[2]));
        // 创建搜索范围
        AABB searchBox = new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
        // 获取范围内的所有实体
        List<T> entities = level.getEntitiesOfClass(entityClass, searchBox); // 使用泛型类型参数T
        if (entities.isEmpty()) {
            return WenyanValue.NULL;
        }
        // 找到最近的实体
        T nearestEntity = entities.get(0); // 使用泛型类型参数T

        if (nearestEntity != null) {
            return new WenyanNativeValue(WenyanType.OBJECT, nearestEntity, false);
        }
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}