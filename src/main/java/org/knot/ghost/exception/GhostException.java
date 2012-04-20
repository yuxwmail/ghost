package org.knot.ghost.exception;

public class GhostException extends RuntimeException {

    private static final long   serialVersionUID = 1L;
    private static final String lineSeparator    = System.getProperty("line.separator");
    private final int           reasonAndSolution;
    private final String        exceptionDesc;

    public GhostException(int exceptionCode, String exceptionDesc, Throwable e){
        super(exceptionMessage(exceptionCode, exceptionDesc), e);
        this.reasonAndSolution = exceptionCode;
        this.exceptionDesc = exceptionDesc;
    }

    public GhostException(int exceptionCode, Throwable e){
        this(exceptionCode, null, e);
    }

    public GhostException(int exceptionCode, String exceptionDesc){
        super(exceptionMessage(exceptionCode, exceptionDesc));
        this.reasonAndSolution = exceptionCode;
        this.exceptionDesc = exceptionDesc;
    }

    public GhostException(int exceptionCode){
        this(exceptionCode, "");
    }

    public GhostException(String exceptionDesc){
        this(GhostExceptionCode.UNDEF.getValue(), exceptionDesc);
    }

    public GhostException(String exceptionDesc, Throwable e){
        this(GhostExceptionCode.UNDEF.getValue(), exceptionDesc, e);
    }

    public String toString() {
        return exceptionMessage(this.reasonAndSolution, this.exceptionDesc);
    }

    private static String exceptionMessage(int exceptionCode, String exceptionDesciption) {
        StringBuilder strBuilder = new StringBuilder("code:").append(exceptionCode);
        if (exceptionDesciption != null && !"".equals(exceptionDesciption)) {
            strBuilder.append(lineSeparator);
            strBuilder.append("desciption: ");
            strBuilder.append(exceptionDesciption);
        }
        return strBuilder.toString();
    }
}
