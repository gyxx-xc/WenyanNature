package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;

/// Marker interface for numeric values in Wenyan language.
public interface IWenyanNumber extends IWenyanValue{
    WenyanType<IWenyanNumber> TYPE = new WenyanType<>(JudouTypeText.Number.string(), IWenyanNumber.class);
}
