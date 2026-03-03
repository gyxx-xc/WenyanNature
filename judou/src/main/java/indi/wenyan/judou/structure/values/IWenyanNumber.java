package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanType;

public interface IWenyanNumber extends IWenyanValue{
    WenyanType<IWenyanNumber> TYPE = new WenyanType<>("number", IWenyanNumber.class);
}
