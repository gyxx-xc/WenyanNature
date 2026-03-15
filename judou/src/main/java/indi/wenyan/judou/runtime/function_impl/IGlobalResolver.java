package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;

public interface IGlobalResolver {
    IWenyanValue getAttribute(String name) throws WenyanException;
}
