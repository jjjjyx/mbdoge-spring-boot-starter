package cn.mbdoge.jyx.event;

/**
 * 建议以枚举的新式实现该类
 * <pre>
 * enum A implements EventType {
 *     event {
 *          public String[] getNames() {
 *                  return new String[] {"arg1", "arg2"};
 *          }
 *     }
 * }
 * </pre>
 */
public interface EventType {

    String[] emptyNames = {};
    /**
     * 返回事件类型参数名称列表
     * @return
     */
    default String[] getNames() {
        return emptyNames;
    }

    /**
     * 返回事件名称
     * @return
     */
    String name();
}


