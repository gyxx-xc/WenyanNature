package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;

public interface IWenyanNumber extends IWenyanValue{
    WenyanType<IWenyanNumber> TYPE = new WenyanType<>(JudouTypeText.Number.string(), IWenyanNumber.class);
}
