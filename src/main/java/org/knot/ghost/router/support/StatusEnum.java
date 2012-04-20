package org.knot.ghost.router.support;

/**
 * status ״̬
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public enum StatusEnum {

    //-1:δ��ʼ�� 0������     1����ѯ
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
