package indi.wenyan;

import com.google.auto.service.AutoService;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.tools.javac.api.BasicJavacTask;

@AutoService(com.sun.source.util.Plugin.class)
public class PackageProviderPlugin implements Plugin {
    @Override
    public String getName() {
        return "PackageProviderPlugin";
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {
        javacTask.addTaskListener(new PackageProviderTaskListener((BasicJavacTask) javacTask));
    }
}
