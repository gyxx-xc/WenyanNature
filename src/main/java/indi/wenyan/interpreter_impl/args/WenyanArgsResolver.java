package indi.wenyan.interpreter_impl.args;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

/// entry point for args resolve
///
/// usage:
/// ```java
/// var args = WenyanArgsSpec.build()
///         .int_().rangeThrow(0, 1000).range(3, 5)
///         .string()
///         .blockpos().limit(0, 3, 0).throwOnFail()
///         .resolve(request);
///
/// int i = args.get(0);
/// String s = args.get(1);
/// ```
public class WenyanArgsResolver {

    private final List<ArgExtractor<?>> extractors;
    private final List<IWenyanValue> args;

    /// package private, should only be called in ArgsSpecBuilder
    WenyanArgsResolver(List<ArgExtractor<?>> extractors, IArgsRequest request) throws WenyanException {
        this.extractors = extractors;
        this.args = request.args();
        if (extractors.size() != args.size())
            throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(extractors.size(), args.size()));
    }

    /// package private, delegate to ArgsSpecBuilder
    ResolvedArgs resolve() throws WenyanException {
        Object[] values = new Object[extractors.size()];
        for (int i = 0; i < extractors.size(); i++) {
            values[i] = extractors.get(i).extract(args.get(i));
        }
        return new ResolvedArgs(values);
    }

    public static <T> ArgsSpecBuilder.Step<T> build() {
        return new ArgsSpecBuilder().first();
    }
}
