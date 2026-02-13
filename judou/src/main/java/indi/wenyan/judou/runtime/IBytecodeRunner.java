package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanThrowException;

public interface IBytecodeRunner {
    void run(int step) throws WenyanThrowException;
}
