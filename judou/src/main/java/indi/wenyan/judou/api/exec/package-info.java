/// package for exec function in game thread
///
/// Since Judou exec the code on a separated thread for performance and safety concern,
/// it provided a way to async the code with game thread, making developing function just like in tick().
///
/// To archive this, the core structure is {@link indi.wenyan.judou.api.exec.request.IHandleableRequest request},
/// which will:
/// - define the function of how to exec(handle)
/// - store the calling context until exec
/// - contain data across tick call
///
/// the request will be adopted with {@link indi.wenyan.judou.api.exec.IRequestCallHandler handler} as
/// {@link indi.wenyan.judou.api.values.IWenyanFunction function}, the detailed process is:
/// 1. program got handler as function and call it
/// 2. handler create a {@link indi.wenyan.judou.api.exec.request.IHandleableRequest request} with context
/// 3. handler add request to the {@link indi.wenyan.judou.api.exec.structure.IExecQueue IExecQueue} belong to
/// {@link indi.wenyan.judou.api.exec.structure.IWenyanPlatform IWenyanPlatform} and block
/// 4. platform will handle the request when ticking

package indi.wenyan.judou.api.exec;