package cn.mbdoge.jyx.jwt.core;

/**
 * @author jyx
 */
public interface DataView {
    interface BaseView {}
    interface UserView extends BaseView{}
    interface AdminView extends UserView {}
    interface Anonymous extends BaseView {}
}
