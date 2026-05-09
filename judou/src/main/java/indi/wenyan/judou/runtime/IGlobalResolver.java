package indi.wenyan.judou.runtime;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.values.IWenyanValue;

public interface IGlobalResolver {
    IWenyanValue getGlobal(String name) throws WenyanException;
}
