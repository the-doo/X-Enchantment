package com.doo.xenchant.attribute;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;

/**
 * 可移除的属性修改
 */
public class LimitTimeModifier extends EntityAttributeModifier {

    private final Entity own;

    private int expire;

    private LimitTimeModifier(String name, double value, Operation operation, int expire, Entity own) {
        super(name, value, operation);
        this.expire = expire;
        this.own = own;
    }

    public static LimitTimeModifier get(String name, double value, Operation operation, int expire, Entity own) {
        return new LimitTimeModifier(name, value, operation, expire, own);
    }

    /**
     * 如果时间到了，则直接返回0，与getOperation()则实现不添加效果
     *
     * @return value
     */
    @Override
    public double getValue() {
        return isExpire() ? 0 : super.getValue();
    }

    /**
     * 如果时间到了，则直接换成添加操作
     *
     * @return operation
     */
    @Override
    public Operation getOperation() {
        return isExpire() ? Operation.ADDITION : super.getOperation();
    }

    /**
     * 重置存在世界
     *
     * @param second 秒数
     */
    public void reset(float second) {
        expire = (int) (own.age + second * 20);
    }

    /**
     * 重置存在世界
     *
     * @return 过期了
     */
    public boolean isExpire() {
        return own.age > expire;
    }
}
