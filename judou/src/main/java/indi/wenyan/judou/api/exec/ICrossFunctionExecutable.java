package indi.wenyan.judou.api.exec;

import indi.wenyan.judou.api.values.IWenyanFunction;

/// {@link IWenyanFunction} that must execute outside the caller (in scheduler / game tick).
public interface ICrossFunctionExecutable extends IWenyanFunction {
}
