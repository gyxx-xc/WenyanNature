package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;

/// Interface representing a Wenyan object type that can create new instances.
public interface IWenyanObjectType extends IWenyanFunction, IWenyanObject {
    WenyanType<IWenyanObjectType> TYPE = new WenyanType<>(JudouTypeText.ObjectType.string(), IWenyanObjectType.class);
}
