package indi.wenyan.content.handler.feature_additions.entity_handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.entity.HandRunnerEntity;
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
 * @className EntityFinderHandler
 * @Description 大寻
 * @date 2025/6/6 15:56
 */
public class EntityFinderHandler<T extends Entity> implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = //搜寻单个实体
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};
    public static final WenyanType[] ARGS_TYPE2=//范围搜寻所有实体 并尝试设置半径
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};
    private final Class<T> entityClass;

    //继承了 大寻 的类专用方法
    public EntityFinderHandler(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        Level level;
        if (context.runner().runner() instanceof HandRunnerEntity runner) {
            level=runner.level();
        }else{
            BlockRunner runner= (BlockRunner) context.runner().runner();
            level=runner.getLevel();
        }
        List<Object> args;
        try {
            args= JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        }catch (Exception e) {
            args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE2);
        }
        double x = (double) args.get(0);
        double y = (double) args.get(1);
        double z = (double) args.get(2);
        // 创建搜索范围
        AABB searchBox = new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
        //当参数数量为4时将搜索范围扩大
        if (args.size() == 4) {
            double radius = (double) args.get(3);
            if (radius<=0){
                radius = 0.01;
            }
            searchBox = new AABB(x-0.5*radius, y-0.5*radius, z-0.5*radius,
                    x+0.5*radius, y+0.5*radius, z+0.5*radius);
        }
        // 获取范围内的所有实体
        List<T> entities = level.getEntitiesOfClass(entityClass, searchBox); // 使用泛型类型参数T
        if (entities.isEmpty()) {
            return WenyanValue.NULL;
        }
        //当参数数量为4时返回列表
        if (args.size() == 4) {
            WenyanArrayObject list=new WenyanArrayObject();
            for (T entity : entities) {
                list.add(new WenyanNativeValue(WenyanType.OBJECT, entity,false));
            }
            return new WenyanNativeValue(WenyanType.LIST, list, false);
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