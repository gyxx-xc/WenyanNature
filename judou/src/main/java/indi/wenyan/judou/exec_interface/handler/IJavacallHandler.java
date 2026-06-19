package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.values.IWenyanFunction;

/// Interface for handlers that bridge between Java and Wenyan code.
/// Provides type information and step calculation.
public interface IJavacallHandler extends IWenyanFunction {
    /// Type identifier for Javacall handlers
    WenyanType<IJavacallHandler> TYPE = new WenyanType<>(JudouTypeText.JavacallHandler.string(), IJavacallHandler.class);

    default WenyanType<?> type() {
        return TYPE;
    }
}
