package indi.wenyan.judou.runtime;

import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;

public interface IGlobalResolver {
    IWenyanValue getGlobal(String name) throws WenyanException;
}
