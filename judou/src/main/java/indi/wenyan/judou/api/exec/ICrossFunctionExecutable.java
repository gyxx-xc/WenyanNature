package indi.wenyan.judou.api.exec;

import indi.wenyan.judou.api.values.IWenyanFunction;

/// the {@link IWenyanFunction WenyanFunction} that require caller to return immediate,
/// as it need to execute outside the caller function (in scheduler, game tick, etc.)
public interface ICrossFunctionExecutable extends IWenyanFunction {
}
