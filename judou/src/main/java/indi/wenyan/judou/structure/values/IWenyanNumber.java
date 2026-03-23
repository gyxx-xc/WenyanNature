package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.utils.language.JudouTypeText;

public interface IWenyanNumber extends IWenyanValue{
    WenyanType<IWenyanNumber> TYPE = new WenyanType<>(JudouTypeText.Number.string(), IWenyanNumber.class);
}
