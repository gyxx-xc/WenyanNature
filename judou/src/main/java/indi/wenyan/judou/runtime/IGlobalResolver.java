package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;

public interface IGlobalResolver {
    IWenyanValue getGlobal(String name) throws WenyanException;
}
