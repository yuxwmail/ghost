package org.knot.ghost.router.support;

/**
 * status 状态
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public enum StatusEnum {

    //-1:未初始化 0：更新     1：查询
    READ((short)1), UPDATE((short)0), INIT((short)-1);

    private Short value;

    private StatusEnum(Short value){
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public void setValue(Short value) {
        this.value = value;
    }
}
