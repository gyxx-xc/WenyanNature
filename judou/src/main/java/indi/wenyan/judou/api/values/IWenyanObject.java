package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.values.exception.WenyanException;

/// Interface representing an object in Wenyan language.
/// Objects have attributes that can be accessed and modified.
public interface IWenyanObject extends IWenyanValue {
    WenyanType<IWenyanObject> TYPE = new WenyanType<>(JudouTypeText.Object.string(), IWenyanObject.class);

    /// Get the attribute of this object.
    ///
    /// Contains all variables and functions
    /// in form of 'this' zhi ''name''.
    ///
    /// @param name the name of the attribute
    /// @return the value of the attribute
    IWenyanValue getAttribute(String name) throws WenyanException;
}
