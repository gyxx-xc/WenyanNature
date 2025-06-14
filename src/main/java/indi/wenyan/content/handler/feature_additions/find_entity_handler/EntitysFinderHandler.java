package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className EntitysFinderHandler
 * @Description TODO 大寻  使用继承了Entity的泛型作为通用范围搜寻所有实体的类
 * @date 2025/6/6 15:56
 */

public class EntitysFinderHandler<T extends Entity> implements JavacallHandler {
    private final Level level;

    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};
    private Class<T> entityClass= (Class<T>) Entity.class;

    public EntitysFinderHandler(Level level) {
        this.level = level;
    }
    //继承了大寻的类专用方法
    public EntitysFinderHandler(Level level, Class<T> entityClass) {
        this.level = level;
        this.entityClass = entityClass;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        double x = Double.parseDouble(String.valueOf(args[0]));
        double y = Double.parseDouble(String.valueOf(args[1]));
        double z = Double.parseDouble(String.valueOf(args[2]));
        double radius = Double.parseDouble(String.valueOf(args[3]));
        if (radius<=0){
            radius = 0.01;
        }
        // 创建搜索范围
        AABB searchBox = new AABB(x-1*radius, y-1*radius, z-1*radius,
                x+1*radius, y+1*radius, z+1*radius);

        // 获取范围内的所有实体
        List<T> entities = level.getEntitiesOfClass(entityClass, searchBox);
        if (entities.isEmpty()) {
            return WenyanNativeValue.NULL;
        }

        if (entities != null) {
            WenyanArrayObject list=new WenyanArrayObject();
            for (T entity : entities) {
                list.add(new WenyanNativeValue(WenyanType.OBJECT, entity,false));
            }
            return new WenyanNativeValue(WenyanType.LIST, list, false);
        }

        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
