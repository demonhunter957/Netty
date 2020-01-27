package netty.unpackStickPackage.solution;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义协议
 */
public class MessageProtocol {
    @Getter
    @Setter
    private int len;

    @Getter
    @Setter
    private byte[] content;
}
