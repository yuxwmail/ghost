package org.knot.ghost.exception;

/**
 * 
 * @author yuxiaowei
 */
public enum GhostExceptionCode {

    UNDEF(000), VALI_FAIL(501), ZK_ERROR(502) , NO_SERVICE(503);

    private int value;

    private GhostExceptionCode(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
